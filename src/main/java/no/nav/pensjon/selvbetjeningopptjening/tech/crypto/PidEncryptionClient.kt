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


@Component
class PidEncryptionClient(
    @Value("\${pid-encryption.endpoint.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${sob.web-client.retry-attempts}") retryAttempts: String
): PingableServiceClient(null, webClientBuilder, retryAttempts){
    fun decrypt(encryptedPid: String?): String? =
        webClient
            .post()
            .uri("$baseUrl/api/decrypt")
            .headers(::setHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(encryptedPid!!)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(
        e: EgressException,
        uri: String
    ): String {
        TODO("Not yet implemented")
    }
    override fun pingPath(): String = "$baseUrl/isAlive"

    override fun setPingHeaders(headers: HttpHeaders) {
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }
    override fun service(): EgressService = PidEncryptionClient.Companion.service

    companion object {
        private val service = EgressService.PID_ENCRYPTION
    }
}