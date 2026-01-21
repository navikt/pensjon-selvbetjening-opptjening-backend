package no.nav.pensjon.selvbetjeningopptjening.common.client

import no.nav.pensjon.selvbetjeningopptjening.tech.selftest.PingResult
import no.nav.pensjon.selvbetjeningopptjening.tech.selftest.Pingable2
import no.nav.pensjon.selvbetjeningopptjening.tech.selftest.ServiceStatus
import no.nav.pensjon.selvbetjeningopptjening.tech.web.EgressException
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

abstract class PingableServiceClient(
    private val baseUrl: String?, // if null then pingPath() must return full ping URL
    webClientBuilder: WebClient.Builder,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable2 {

    abstract fun pingPath(): String

    abstract fun setPingHeaders(headers: HttpHeaders)

    protected val webClient: WebClient =
        baseUrl?.let { webClientBuilder.baseUrl(it).build() } ?: webClientBuilder.build()

    override fun ping(): PingResult {
        val path = pingPath()
        val uri = baseUrl?.let { "/$path" } ?: path
        val fullUrl = baseUrl?.let { "$it/$path" } ?: path

        return try {
            val responseBody =
                webClient
                    .get()
                    .uri(uri)
                    .headers(::setPingHeaders)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .retryWhen(retryBackoffSpec(fullUrl))
                    .block()
                    ?: ""

            up(endpoint = fullUrl, message = responseBody)
        } catch (e: EgressException) {
            // Happens if failing to get an access token
            down(uri = fullUrl, e)
        } catch (e: WebClientRequestException) {
            down(uri = fullUrl, e)
        } catch (e: WebClientResponseException) {
            down(uri = fullUrl, message = e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun up(endpoint: String, message: String) =
        PingResult(
            service = service(),
            status = ServiceStatus.UP,
            endpoint,
            message
        )

    private fun down(uri: String, e: Throwable) =
        down(
            uri,
            message = e.message ?: "Failed calling ${service()}"
        )

    private fun down(uri: String, message: String) =
        PingResult(
            service = service(),
            status = ServiceStatus.DOWN,
            endpoint = uri,
            message
        )
}
