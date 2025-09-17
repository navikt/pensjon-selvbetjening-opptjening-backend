package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Component
import kotlin.let

/**
 * Extracts the person-ID (PID) from the security context.
 * This is the PID of the user who is authenticated according to the access token (JWT).
 * The PID is contained in the token, which has been put in the security context by
 * Spring Security following successful authentication.
 */
@Component
class SecurityContextPidExtractor {

    fun pid(): Pid? = pidFromSecurityContext()?.let(::Pid)

    companion object {
        private const val CLAIM_KEY = "pid"

        fun pidFromSecurityContext(): String? = SecurityContextClaimExtractor.claim(CLAIM_KEY) as? String
    }
}
