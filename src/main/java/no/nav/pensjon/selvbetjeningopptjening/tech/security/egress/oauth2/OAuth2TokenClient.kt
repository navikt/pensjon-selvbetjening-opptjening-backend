package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.common.client.ExternalServiceClient
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.MetricResult
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenData
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenDataGetter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.validation.ExpirationChecker
import no.nav.pensjon.selvbetjeningopptjening.tech.web.EgressException
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import kotlin.also
import kotlin.jvm.java

abstract class OAuth2TokenClient(
    private val tokenEndpoint: String,
    webClientBuilder: WebClient.Builder,
    retryAttempts: String,
    private val expirationChecker: ExpirationChecker
) : ExternalServiceClient(retryAttempts), TokenDataGetter {

    private val log = KotlinLogging.logger {}
    private val webClient = webClientBuilder.baseUrl(tokenEndpoint).build()

    override fun service() = service

    override fun getTokenData(accessParameter: TokenAccessParameter, audience: String): TokenData {
        log.debug { "Fetching token for audience '$audience'" }

        return try {
            val body: OAuth2TokenDto = webClient
                .post()
                .uri("")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(prepareTokenRequestBody(accessParameter, audience))
                .retrieve()
                .bodyToMono(OAuth2TokenDto::class.java)
                .retryWhen(retryBackoffSpec(tokenEndpoint))
                .block()!!
                .also { countCalls(MetricResult.OK) }
            // Note: Do not use .body instead of .bodyValue, since this results in chunked encoding,
            // which the endpoint may not support, resulting in 404 Not Found

            log.debug { "Token obtained for audience '$audience'" }
            OAuth2TokenDataMapper.map(body, expirationChecker.time())
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $tokenEndpoint", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    protected fun isExpired(token: TokenData): Boolean =
        expirationChecker.isExpired(token.issuedTime, token.expiresInSeconds)

    protected abstract fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter, audience: String
    ): MultiValueMap<String, String>

    private companion object {
        private val service = EgressService.OAUTH2_TOKEN
    }
}
