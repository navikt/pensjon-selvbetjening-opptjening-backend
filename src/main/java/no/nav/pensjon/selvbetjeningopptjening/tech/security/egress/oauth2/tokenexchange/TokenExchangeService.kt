package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange.client.TokenExchangeClient
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.CacheKey
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.stereotype.Service

@Service
class TokenExchangeService(
    val client: TokenExchangeClient,
    private val loggedInPidProvider: SecurityContextPidExtractor
) : EgressTokenGetter {

    /**
     * audience is the value of an 'app ID' property, e.g. pensjon.representasjon.app-id
     */
    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        val accessParameter = ingressToken?.let(TokenAccessParameter::tokenExchange)
            ?: throw IllegalArgumentException("Missing ingressToken")

        val tokenValue = loggedInPidProvider.pid()?.let {
            client.exchange(accessParameter, CacheKey(scope = audience, pid = it)).accessToken
        } ?: throw IllegalStateException("Missing PID of logged in user")

        return RawJwt(tokenValue)
    }
}
