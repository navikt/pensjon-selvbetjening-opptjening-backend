package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.crypto.PidEncryptionService
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.Metrics
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjonstype
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonService
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentertRolle
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.AccessDeniedReason
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.AuthTypeDeducer
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.SecurityContextPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.web.CustomHttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength

@Component
class SecurityContextEnricher(
    val tokenSuppliers: EgressTokenSuppliersByService,
    private val authTypeDeducer: AuthTypeDeducer,
    private val pidExtractor: SecurityContextPidExtractor,
    private val pidDecrypter: PidEncryptionService,
    private val representasjonService: RepresentasjonService
) {
    fun enrichAuthentication(request: HttpServletRequest, response: HttpServletResponse) {
        with(SecurityContextHolder.getContext()) {
            if (authentication == null) {
                authentication = anonymousAuthentication()
            } else {
                authentication = enrichStep1(authentication!!)
                authentication = enrichStep2(authentication as EnrichedAuthentication, request)
                authentication = applyPotentialFullmakt(authentication!!, request)
            }
        }
    }

    private fun anonymousAuthentication() =
        EnrichedAuthentication(
            initialAuth = null,
            authType = authTypeDeducer.deduce(isRepresentant = false),
            egressTokenSuppliersByService = tokenSuppliers,
            target = RepresentasjonTarget(rolle = RepresentertRolle.NONE)
        )

    /**
     * enrichStep1 is a precondition for the personUnderVeiledning call in enrichStep2.
     */
    private fun enrichStep1(auth: Authentication) =
        EnrichedAuthentication(
            initialAuth = auth,
            authType = authTypeDeducer.deduce(isRepresentant = false),
            egressTokenSuppliersByService = tokenSuppliers,
            target = selv()
        )

    private fun enrichStep2(auth: EnrichedAuthentication, request: HttpServletRequest) =
        EnrichedAuthentication(
            initialAuth = auth,
            authType = auth.authType,
            egressTokenSuppliersByService = tokenSuppliers,
            target = personUnderVeiledning(request) ?: selv()
        )

    private fun enrichWithFullmakt(auth: Authentication, fullmaktgiverPid: Pid) =
        EnrichedAuthentication(
            initialAuth = auth,
            authType = authTypeDeducer.deduce(isRepresentant = true),
            egressTokenSuppliersByService = tokenSuppliers,
            target = fullmaktgiver(pid = fullmaktgiverPid)
        )

    private fun selv(): RepresentasjonTarget =
        selv(pid = pidExtractor.pid())

    private fun personUnderVeiledning(request: HttpServletRequest): RepresentasjonTarget? =
        onBehalfOfPid(request)
            ?.let { personUnderVeiledning(pid = Pid(decrypt(it))) }

    private fun applyPotentialFullmakt(
        auth: Authentication,
        request: HttpServletRequest
    ): Authentication =
        onBehalfOfPid(request.cookies)?.let {
            if (validRepresentasjonForhold(pid = it))
                enrichWithFullmakt(auth, fullmaktgiverPid = it).also { countOnBehalfOfEvent(result = "ok") }
            else
                invalidRepresentasjonForhold()
        } ?: auth

    /**
     * NB: Dette støtter ikke brukstilfellet der veileder er logget inn på vegne av en fullmektig.
     * Dette fordi pensjon-representasjon henter ut PID fra TokenX-tokenet (som ikke finnes når veileder er logget inn).
     */
    private fun validRepresentasjonForhold(pid: Pid): Boolean =
        representasjonService.hasValidRepresentasjonsforhold(
            fullmaktGiverPid = pid,
            representasjonstyper = Representasjonstype.VALID_SKRIV_TYPES
        ).isValid

    private fun onBehalfOfPid(cookies: Array<Cookie>?): Pid? =
        cookies.orEmpty()
            .filter { ON_BEHALF_OF_COOKIE_NAME.equals(it.name, ignoreCase = true) }
            .map { Pid((decrypt(it.value.orEmpty()))) }
            .firstOrNull()

    private fun decrypt(value: String): String =
        if (value.contains(ENCRYPTION_MARK))
            pidDecrypter.decryptPid(encryptedPid = value).orEmpty()
        else
            value

    private companion object {
        private const val ENCRYPTION_MARK = "."
        private const val ON_BEHALF_OF_COOKIE_NAME = "nav-obo"


        private fun onBehalfOfPid(request: HttpServletRequest): String? =
            headerPid(request) ?: request.getParameter("pid")

        private fun headerPid(request: HttpServletRequest): String? =
            request.getHeader(CustomHttpHeaders.PID)?.let {
                when {
                    hasLength(it).not() -> null
                    else -> it
                }
            }

        /**
         * NB: pid is null when veileder is logged in (veileder is identified by Nav-ID, not PID).
         */
        private fun selv(pid: Pid?) =
            RepresentasjonTarget(pid, rolle = RepresentertRolle.SELV)

        private fun fullmaktgiver(pid: Pid) =
            RepresentasjonTarget(pid, rolle = RepresentertRolle.FULLMAKT_GIVER)

        private fun personUnderVeiledning(pid: Pid) =
            RepresentasjonTarget(pid, rolle = RepresentertRolle.UNDER_VEILEDNING)

        private fun invalidRepresentasjonForhold(): Nothing {
            countOnBehalfOfEvent(result = "avvist")
            throw AccessDeniedException(AccessDeniedReason.INVALID_REPRESENTASJON.name)
        }

        private fun countOnBehalfOfEvent(result: String) {
            Metrics.countEvent(eventName = "obo", result)
        }
    }
}
