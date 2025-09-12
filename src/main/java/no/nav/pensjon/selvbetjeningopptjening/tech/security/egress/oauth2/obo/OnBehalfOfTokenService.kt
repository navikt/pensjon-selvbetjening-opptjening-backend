package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.entra.EntraIdUtil.getDefaultScope
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo.client.OnBehalfOfTokenClient
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.CacheKey
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import org.springframework.stereotype.Service

@Service
class OnBehalfOfTokenService(
    val client: OnBehalfOfTokenClient,
    private val pidProvider: TargetPidExtractor
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        val scope = getDefaultScope(audience)

        val accessParameter = ingressToken?.let(TokenAccessParameter::jwtBearer)
            ?: throw IllegalArgumentException("Missing ingressToken")

        val tokenValue = pidProvider.pid().let {
            client.exchange(accessParameter, CacheKey(scope, it)).accessToken
        }

        return RawJwt(tokenValue)
    }
}
