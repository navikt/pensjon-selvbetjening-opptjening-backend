package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.common.client.PingableServiceClient
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.MetricResult
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjonstype
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.RepresentasjonClient
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl.PensjonRepresentasjonMapper.fromDto
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl.PensjonRepresentasjonRequest
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl.PensjonRepresentasjonResult
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EgressAccess
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
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

/**
 * Client for accessing the 'pensjon-representasjon' service
 * (see github.com/navikt/pensjon-fullmakt)
 */
@Component
class PensjonRepresentasjonClient(
    @param:Value($$"${pensjon-representasjon.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value($$"${sob.web-client.retry-attempts}") retryAttempts: String
) : PingableServiceClient(null, webClientBuilder, retryAttempts),
    RepresentasjonClient {

    private val log = KotlinLogging.logger {}

    override fun hasValidRepresentasjonsforhold(representertPid: String, representasjonstyper: List<Representasjonstype>): Representasjon {
        val uri = "$baseUrl$PATH"
        log.debug { "POST to URI: '$uri'" }

        val requestBody = PensjonRepresentasjonRequest(
            representertPid = representertPid,
            representantPid = null,
            validRepresentasjonstyper = representasjonstyper.map { it.name },
            includeRepresentertNavn = false
        )

        return try {
            webClient
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers { setHeaders(it) }
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PensjonRepresentasjonResult::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?.let(::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: noRepresentasjonForhold()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun service(): EgressService = service

    override fun pingPath(): String = "$baseUrl/actuator/health/liveness"

    override fun setPingHeaders(headers: HttpHeaders) {
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val PATH = "/representasjon/hasValidRepresentasjonsforhold"


        private val service = EgressService.PENSJON_REPRESENTASJON

        private fun noRepresentasjonForhold() =
            Representasjon(isValid = false, fullmaktGiverNavn = "")
    }
}
