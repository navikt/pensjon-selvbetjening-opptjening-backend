package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

interface TokenDataGetter {
    fun getTokenData(accessParameter: TokenAccessParameter, audience: String): TokenData
}
