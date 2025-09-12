package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo.client.entra

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.OAuth2ParameterBuilder
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo.client.OnBehalfOfCredentials
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo.client.OnBehalfOfTokenClient
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
@Qualifier("obo")
class EntraOnBehalfOfTokenClient(
    @param:Value($$"${azure.openid-config.token-endpoint}") private val tokenEndpoint: String,
    webClientBuilder: WebClient.Builder,
    expirationChecker: ExpirationChecker,
    private val credentials: OnBehalfOfCredentials,
    @Value($$"${sob.web-client.retry-attempts}") retryAttempts: String
) : CacheAwareTokenClient(
    tokenEndpoint,
    webClientBuilder,
    retryAttempts,
    expirationChecker
), OnBehalfOfTokenClient {

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String
    ): MultiValueMap<String, String> =
        OAuth2ParameterBuilder()
            .tokenAccessParameter(accessParameter)
            .clientId(credentials.clientId)
            .clientSecret(credentials.clientSecret)
            .scope(audience)
            .onBehalfOfTokenRequestMap()

    override fun exchange(accessParameter: TokenAccessParameter, cacheKey: CacheKey) =
        getTokenData(accessParameter, scope = cacheKey.scope, user = cacheKey.pid.pid)
}
