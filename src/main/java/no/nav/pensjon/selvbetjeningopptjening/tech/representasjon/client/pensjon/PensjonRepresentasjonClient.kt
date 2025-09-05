package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.common.client.PingableServiceClient
import no.nav.pensjon.selvbetjeningopptjening.consumer.CustomHttpHeaders
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.MetricResult
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.RepresentasjonClient
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl.PensjonRepresentasjonMapper.fromDto
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl.PensjonRepresentasjonResult
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
import org.springframework.web.util.UriComponentsBuilder

/**
 * Client for accessing the 'pensjon-representasjon' service
 * (see github.com/navikt/pensjon-fullmakt)
 */
@Component
class PensjonRepresentasjonClient(
    @param:Value("\${pensjon-representasjon.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${sob.web-client.retry-attempts}") retryAttempts: String
) : PingableServiceClient(null, webClientBuilder, retryAttempts),
    RepresentasjonClient {

    private val log = KotlinLogging.logger {}

    override fun hasValidRepresentasjonsforhold(fullmaktGiverPid: Pid): Representasjon {
        val uri = uri()
        log.debug { "GET from URI: '$uri'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers { setHeaders(it, fullmaktGiverPid) }
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

    private fun uri(): String =
        UriComponentsBuilder.fromUriString(baseUrl)
            .path(PATH)
            .queryParam(VALID_REPRESENTASJON_TYPER_QUERY_PARAM_NAME, representasjonTypeListe)
            .queryParam(INCLUDE_FULLMAKT_GIVER_NAVN_QUERY_PARAM_NAME, false)
            .build()
            .toUriString()

    private fun setHeaders(headers: HttpHeaders, fullmaktGiverPid: Pid? = null) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        fullmaktGiverPid?.let { headers[CustomHttpHeaders.FULLMAKTSGIVER_PID] = it.pid }
    }

    companion object {
        private const val PATH = "/representasjon/hasValidRepresentasjonsforhold"
        private const val INCLUDE_FULLMAKT_GIVER_NAVN_QUERY_PARAM_NAME = "includeFullmaktsgiverNavn"
        private const val VALID_REPRESENTASJON_TYPER_QUERY_PARAM_NAME = "validRepresentasjonstyper"

        private val representasjonTypeListe: List<String> =
            listOf(
                "PENSJON_FULLSTENDIG",
                "PENSJON_SKRIV",
                "PENSJON_PENGEMOTTAKER",
                "PENSJON_VERGE",
                "PENSJON_VERGE_PENGEMOTTAKER"
            )

        private val service = EgressService.PENSJON_REPRESENTASJON

        private fun noRepresentasjonForhold() =
            Representasjon(isValid = false, fullmaktGiverNavn = "")
    }
}
