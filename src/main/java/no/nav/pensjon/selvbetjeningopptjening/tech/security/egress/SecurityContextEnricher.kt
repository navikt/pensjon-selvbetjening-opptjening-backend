package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.selvbetjeningopptjening.consumer.CustomHttpHeaders
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.crypto.PidEncryptionService
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.Metrics
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonService
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentertRolle
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.AccessDeniedReason
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.AuthTypeDeducer
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength
import kotlin.also
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlin.let
import kotlin.text.contains
import kotlin.text.equals

@Component
class SecurityContextEnricher(
    val tokenSuppliers: EgressTokenSuppliersByService,
    private val authTypeDeducer: AuthTypeDeducer,
    private val securityContextPidExtractor: SecurityContextPidExtractor,
    private val pidDecrypter: PidEncryptionService,
    private val representasjonService: RepresentasjonService
) {
    fun enrichAuthentication(request: HttpServletRequest, response: HttpServletResponse) {
        with(SecurityContextHolder.getContext()) {
            if (authentication == null) {
                authentication = anonymousAuthentication()
            } else {
                authentication = enrichStep1(authentication, request)
                authentication = enrichStep2(authentication as EnrichedAuthentication, request)
                authentication = applyPotentialFullmakt(authentication, request)
            }
        }
    }

    private fun enrichStep1(auth: Authentication, request: HttpServletRequest) =
        EnrichedAuthentication(
            initialAuth = auth,
            authType = authTypeDeducer.deduce(isRepresentant = false),
            egressTokenSuppliersByService = tokenSuppliers,
            target = selv()
        )

    private fun enrichStep2(auth: EnrichedAuthentication, request: HttpServletRequest): EnrichedAuthentication {
        val kryptertPid = veiledetPid(request)
        var veiledetPid: Pid?
        if (kryptertPid?.contains(ENCRYPTION_MARK) == true) {
            veiledetPid = Pid(pidDecrypter.decryptPid(kryptertPid))
        } else {
            veiledetPid = kryptertPid?.let { Pid(it) }
        }
        return EnrichedAuthentication(
            initialAuth = auth,
            authType = auth.authType,
            egressTokenSuppliersByService = tokenSuppliers,
            target = veiledetPid?.let(::personUnderVeiledning) ?: selv()
        )
    }

    private fun veiledetPid(request: HttpServletRequest): String? =
        ((headerPid(request)
            ?: request.getParameter("pid")))

    private fun applyPotentialFullmakt(
        auth: Authentication,
        request: HttpServletRequest
    ): Authentication =
        onBehalfOfPid(request.cookies)?.let {
            if (validRepresentasjonForhold(it))
                enrichWithFullmakt(auth, it).also { Metrics.countEvent(eventName = "obo", result = "ok") }
            else
                invalidRepresentasjonForhold()
        } ?: auth

    /**
     * NB: Dette støtter ikke brukstilfellet der veileder er logget inn på vegne av en fullmektig.
     * Dette fordi pensjon-representasjon henter ut PID fra TokenX-tokenet (som ikke finnes når veileder er logget inn).
     */
    private fun validRepresentasjonForhold(pid: Pid) =
        representasjonService.hasValidRepresentasjonsforhold(pid).isValid

    private fun enrichWithFullmakt(auth: Authentication, fullmaktGiverPid: Pid) =
        EnrichedAuthentication(
            initialAuth = auth,
            authType = authTypeDeducer.deduce(isRepresentant = true),
            egressTokenSuppliersByService = tokenSuppliers,
            target = RepresentasjonTarget(pid = fullmaktGiverPid, rolle = RepresentertRolle.FULLMAKT_GIVER)
        )

    private fun selv() =
        RepresentasjonTarget(
            pid = securityContextPidExtractor.pid(),
            rolle = RepresentertRolle.SELV
        )

    private fun anonymousAuthentication() =
        EnrichedAuthentication(
            initialAuth = null,
            authType = authTypeDeducer.deduce(isRepresentant = false),
            egressTokenSuppliersByService = tokenSuppliers,
            target = RepresentasjonTarget(rolle = RepresentertRolle.NONE)
        )

    private fun headerPid(request: HttpServletRequest): String? =
        request.getHeader(CustomHttpHeaders.PID)?.let {
            when {
                hasLength(it).not() -> null
                else -> it
            }
        }

    private fun onBehalfOfPid(cookies: Array<Cookie>?): Pid? =
        cookies.orEmpty()
            .filter { ON_BEHALF_OF_COOKIE_NAME.equals(it.name, ignoreCase = true) }
            .map { Pid(it.value) }
            .firstOrNull()

    private companion object {
        const val ENCRYPTION_MARK = "."
        private const val ON_BEHALF_OF_COOKIE_NAME = "nav-obo"

        private fun personUnderVeiledning(pid: Pid) =
            RepresentasjonTarget(pid, rolle = RepresentertRolle.UNDER_VEILEDNING)

        private fun invalidRepresentasjonForhold(): Nothing {
            Metrics.countEvent(eventName = "obo", result = "avvist")
            throw AccessDeniedException(AccessDeniedReason.INVALID_REPRESENTASJON.name)
        }
    }
}
