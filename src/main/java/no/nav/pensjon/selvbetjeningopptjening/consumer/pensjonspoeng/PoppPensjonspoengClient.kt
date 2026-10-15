package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.acl.PoppPensjonspoengResult
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.acl.PoppPensjonspoengSpec
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.PensjonspoengClient
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.popp.ErrorHandler.serviceException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.popp.ErrorHandler.specificException
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EgressAccess.token
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.util.Constants
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class PoppPensjonspoengClient(
    @Qualifier("epoch-support") private val webClient: WebClient,
    @Value($$"${popp.url}") private val baseUrl: String
) : PensjonspoengClient {

    private val log = KotlinLogging.logger { }

    override fun getPensjonspoengListe(pid: Pid): List<Pensjonspoeng> {
        val uri = "$baseUrl/$HENT_PATH"
        log.debug { "POST to URL: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(PoppPensjonspoengSpec(fnr = pid.pid))
                .retrieve()
                .bodyToMono<PoppPensjonspoengResult>()
                .block()
                ?.pensjonspoeng.orEmpty().map { it.fromDto() }
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
        PingInfo("REST", "POPP pensjonspoeng", "$baseUrl/$PING_PATH")

    private companion object {
        private const val CONSUMED_SERVICE = "PROPOPP019 hentPensjonspoengListe"
        private const val PATH = "popp/api/pensjonspoeng"
        private const val HENT_PATH = "$PATH/hent"
        private const val PING_PATH = "$PATH/ping"

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(token(EgressService.PENSJONSOPPTJENING).value)
            headers[Constants.NAV_CALL_ID] = MDC.get(Constants.NAV_CALL_ID)
        }
    }
}