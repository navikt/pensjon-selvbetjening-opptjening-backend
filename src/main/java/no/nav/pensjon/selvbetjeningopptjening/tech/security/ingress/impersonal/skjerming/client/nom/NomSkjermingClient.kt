package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.client.nom

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.common.client.PingableServiceClient
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.MetricResult
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EgressAccess
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.client.SkjermingClient
import no.nav.pensjon.selvbetjeningopptjening.tech.selftest.PingResult
import no.nav.pensjon.selvbetjeningopptjening.tech.selftest.ServiceStatus
import no.nav.pensjon.selvbetjeningopptjening.tech.trace.TraceAid
import no.nav.pensjon.selvbetjeningopptjening.tech.web.CustomHttpHeaders
import no.nav.pensjon.selvbetjeningopptjening.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import kotlin.also
import kotlin.jvm.java

/**
 * Client for accessing the 'skjermede-personer-pip' service
 * (see https://github.com/navikt/skjerming/tree/main/apps/skjermede-personer-pip)
 * NOM = Nav organisasjonsmaster
 */
@Component
class NomSkjermingClient(
    @param:Value($$"${skjermede-personer.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value($$"${sob.web-client.retry-attempts}") retryAttempts: String
) : PingableServiceClient(baseUrl, webClientBuilder, retryAttempts),
    SkjermingClient {

    private val log = KotlinLogging.logger {}

    override fun personErTilgjengelig(pid: Pid): Boolean {
        val uri = "/$API_RESOURCE"
        log.debug { "POST to URI: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .bodyValue(NomSkjermingSpec(pid.pid))
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(Boolean::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                .also { countCalls(MetricResult.OK) } == false
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val uri = pingPath()

        return try {
            webClient
                .options()
                .uri(uri)
                .headers(::setPingHeaders)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(retryBackoffSpec(uri))
                .block()

            PingResult(
                service,
                status = ServiceStatus.UP,
                endpoint = "$baseUrl$uri",
                message = "Ping OK"
            )
        } catch (e: EgressException) {
            // Happens if failing to get an access token
            down(e)
        } catch (e: WebClientRequestException) {
            down(e)
        } catch (e: WebClientResponseException) {
            down(e.responseBodyAsString)
        }
    }

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
    }

    override fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun pingPath(): String = "/$API_RESOURCE"

    override fun service(): EgressService = service

    private fun down(e: Throwable) = down(message = e.message ?: "Failed calling $service")

    private fun down(message: String) = PingResult(
        service,
        status = ServiceStatus.DOWN,
        endpoint = "$baseUrl${pingPath()}",
        message
    )

    companion object {
        private const val API_RESOURCE = "skjermet"
        private val service = EgressService.SKJERMEDE_PERSONER
    }
}
