package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.acl.PoppOpptjeningsgrunnlagResult
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.acl.PoppOpptjeningsgrunnlagSpec
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.OpptjeningClient
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.popp.ErrorHandler.serviceException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.popp.ErrorHandler.specificException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.grunnlag.OpptjeningsgrunnlagSpec
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EgressAccess.token
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.util.Constants
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class PoppOpptjeningClient(
    private val webClient: WebClient,
    @Value($$"${popp.url}") private val baseUrl: String
) : OpptjeningClient {

    private val log = KotlinLogging.logger { }

    override fun getInntektListeFromOpptjeningsgrunnlag(spec: OpptjeningsgrunnlagSpec): List<Inntekt> {
        val uri = "$baseUrl/$HENT_PATH"
        log.debug { "POST to URL: '$uri'" }
        val body = PoppOpptjeningsgrunnlagSpec.fromInternalValue(spec)

        return try {
            webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(body)
                .retrieve()
                .bodyToMono<PoppOpptjeningsgrunnlagResult>()
                .block()
                ?.opptjeningsGrunnlag?.inntektListe.orEmpty().map { it.fromDto() }
        } catch (e: WebClientResponseException) {
            throw serviceException(e, CONSUMED_SERVICE)
        } catch (e: RuntimeException) {
            throw specificException(e, CONSUMED_SERVICE)
        }
    }

    override fun ping() {
        try {
            webClient
                .get()
                .uri("$baseUrl/$PING_PATH")
                .headers(::setHeaders)
                .retrieve()
                .toBodilessEntity()
                .block()
        } catch (e: WebClientResponseException) {
            throw serviceException(e, CONSUMED_SERVICE)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw specificException(e, CONSUMED_SERVICE)
        }
    }

    override fun getPingInfo() =
        PingInfo("REST", "POPP opptjeningsgrunnlag", "$baseUrl/$PING_PATH")

    private companion object {
        private const val CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag"
        private const val PATH = "popp/api/opptjeningsgrunnlag"
        private const val HENT_PATH = "$PATH/hent"
        private const val PING_PATH = "$PATH/ping"

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(token(EgressService.PENSJONSOPPTJENING).value)
            headers[Constants.NAV_CALL_ID] = MDC.get(Constants.NAV_CALL_ID)
        }
    }
}