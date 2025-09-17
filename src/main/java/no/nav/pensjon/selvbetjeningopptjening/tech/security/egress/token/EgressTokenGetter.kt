package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

/**
 * Gets a token for authentication in outgoing (egress) calls.
 */
interface EgressTokenGetter {
    fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt
}
