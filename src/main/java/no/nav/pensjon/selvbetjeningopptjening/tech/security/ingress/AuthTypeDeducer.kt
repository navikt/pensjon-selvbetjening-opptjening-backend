package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.AuthType
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Component

/**
 * Deduces the authentication type based on claims in the security context.
 */
@Component
class AuthTypeDeducer {

    fun deduce(isRepresentant: Boolean): AuthType =
        when {
            claimExists(PID_CLAIM_KEY) -> if (isRepresentant) AuthType.REPRESENTANT else AuthType.PERSON_SELF
            claimExists(NAV_IDENT_CLAIM_KEY) -> AuthType.NAV_ANSATT
            else -> AuthType.NAV_MACHINE
        }

    companion object {
        private const val PID_CLAIM_KEY = "pid"
        private const val NAV_IDENT_CLAIM_KEY = "NAVident"

        fun claimExists(key: String): Boolean =
            SecurityContextClaimExtractor.claim(key) != null
    }
}
