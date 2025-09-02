package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit

import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Component

@Component
class SecurityContextNavIdExtractor {

    fun id(): String = idFromSecurityContext() ?: ""

    companion object {
        const val CLAIM_KEY = "NAVident"

        private fun idFromSecurityContext(): String? = SecurityContextClaimExtractor.claim(CLAIM_KEY) as? String
    }
}
