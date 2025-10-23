package no.nav.pensjon.selvbetjeningopptjening.tech.crypto

import no.nav.pensjon.selvbetjeningopptjening.common.client.PingableServiceClient
import no.nav.pensjon.selvbetjeningopptjening.consumer.CustomHttpHeaders
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EgressAccess
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.trace.TraceAid
import no.nav.pensjon.selvbetjeningopptjening.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.security.core.AuthenticationException

@Component
class PidEncryptionClient(
    @Value("\${pid-encryption.endpoint.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${sob.web-client.retry-attempts}") retryAttempts: String
): PingableServiceClient(null, webClientBuilder, retryAttempts){
    fun decrypt(encryptedPid: String?): String? =
        try {
            webClient
                .post()
                .uri("$baseUrl/api/decrypt")
                .headers(::setHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(encryptedPid!!)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        } catch (e: AuthenticationException)  {
            throw EgressException("Failed obtaining access token for $baseUrl/api/decrypt", e)
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $baseUrl/api/decrypt", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service()).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun pingPath(): String = "$baseUrl/isAlive"

    override fun setPingHeaders(headers: HttpHeaders) {
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun service(): EgressService = EgressService.PID_ENCRYPTION

}