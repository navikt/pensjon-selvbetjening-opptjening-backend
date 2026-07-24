package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.common.exception.NotFoundException
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt.SecurityContextClaimExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.web.CustomHttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.filter.GenericFilterBean
import kotlin.collections.orEmpty

@Component
class ImpersonalAccessFilter(
    private val pidGetter: TargetPidExtractor,
    private val groupMembershipService: GroupMembershipService,
    private val auditor: Auditor
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (hasPid(request as HttpServletRequest)) {
            val pid = pidGetter.pid()
            val personalAccess = accessAsApplication().not()

            try {
                if (personalAccess && groupMembershipService.innloggetBrukerHarTilgang(pid).not()) {
                    forbidden(response as HttpServletResponse)
                    return
                }
            } catch (_: NotFoundException) {
                notFound(response as HttpServletResponse)
                return
            }

            if (personalAccess) {
                auditor.audit(onBehalfOfPid = pid, requestUri = request.requestURI)
            }
        }

        chain.doFilter(request, response)
    }

    private fun accessAsApplication(): Boolean =
        rolesFromSecurityContext()?.map { it.toString() }.orEmpty().contains("access_as_application")

    private fun hasPid(request: HttpServletRequest): Boolean =
        hasLength(request.getHeader(CustomHttpHeaders.PID))
                || hasLength(request.getParameter("pid")) //TODO remove this line when PID no longer in URL

    private fun forbidden(response: HttpServletResponse) {
        "Adgang nektet pga. manglende gruppemedlemskap".let {
            log.warn { it }
            response.sendError(HttpStatus.FORBIDDEN.value(), it)
        }
    }

    private fun notFound(response: HttpServletResponse) {
        "Person ikke funnet".let {
            log.info { it }
            response.sendError(HttpStatus.NOT_FOUND.value(), it)
        }
    }

    private companion object {
        private const val CLAIM_KEY = "roles"

        private fun rolesFromSecurityContext() = SecurityContextClaimExtractor.claim(CLAIM_KEY) as? List<*>
    }
}