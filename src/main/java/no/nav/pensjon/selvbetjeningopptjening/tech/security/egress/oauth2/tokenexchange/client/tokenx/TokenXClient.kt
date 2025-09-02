package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange.client.tokenx

import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.OAuth2ParameterBuilder
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange.TokenExchangeCredentials
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange.client.TokenExchangeClient
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.CacheAwareTokenClient
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.CacheKey
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient

@Component
@Qualifier("token-x")
class TokenXClient(
    @param:Value("\${token-x.token.endpoint}") private val tokenEndpoint: String,
    webClientBuilder: WebClient.Builder,
    expirationChecker: ExpirationChecker,
    private val credentials: TokenExchangeCredentials,
    @Value("\${sob.web-client.retry-attempts}") retryAttempts: String
) : CacheAwareTokenClient(
    tokenEndpoint,
    webClientBuilder,
    retryAttempts,
    expirationChecker
), TokenExchangeClient {

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String // format: "cluster:namespace:app"
    ): MultiValueMap<String, String> =
        OAuth2ParameterBuilder()
            .tokenAccessParameter(accessParameter)
            .clientId(credentials.clientId)
            .tokenAudience(audience)
            .tokenRequestAudience(tokenEndpoint)
            .jwk(RSAKey.parse(credentials.jwk))
            .tokenExchangeRequestMap()

    override fun exchange(accessParameter: TokenAccessParameter, cacheKey: CacheKey) =
        getTokenData(accessParameter, scope = cacheKey.scope, user = cacheKey.pid.pid)
}
