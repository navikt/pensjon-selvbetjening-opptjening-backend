package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.SecurityContextEnricher
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.Responder.respondForbidden
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.filter.GenericFilterBean

/**
 * Servlet filter which augments Spring Security authentication data as required by the application.
 */
class AuthenticationEnricherFilter(private val enricher: SecurityContextEnricher) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpResponse = response as HttpServletResponse

        try {
            enricher.enrichAuthentication(request as HttpServletRequest, httpResponse)
        } catch (e: AccessDeniedException) {
            handleAccessDenied(response = httpResponse, reason = e.message ?: AccessDeniedReason.NONE.name)
            return
        }

        chain.doFilter(request, response)
    }

    private fun handleAccessDenied(response: HttpServletResponse, reason: String) {
        log.warn { "Access denied - $reason" }
        respondForbidden(response, reason)
    }
}
