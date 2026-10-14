package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.acl.PoppRestpensjonResult
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.acl.PoppRestpensjonSpec
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.RestpensjonClient
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
class PoppRestpensjonClient(
    @Qualifier("epoch-support") private val webClient: WebClient,
    @Value($$"${popp.url}") private val baseUrl: String
) : RestpensjonClient {

    private val log = KotlinLogging.logger { }

    override fun getRestpensjonListe(pid: Pid): List<Restpensjon> {
        val uri = "$baseUrl/$HENT_PATH"
        log.debug { "POST to URL: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(PoppRestpensjonSpec(fnr = pid.pid))
                .retrieve()
                .bodyToMono<PoppRestpensjonResult>()
                .block()
                ?.restpensjoner?.map { it.fromDto() }.orEmpty()
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
        PingInfo("REST", "POPP restpensjon", "$baseUrl/$PING_PATH")

    private companion object {
        private const val CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner"
        private const val PATH = "popp/api/restpensjon"
        private const val HENT_PATH = "$PATH/hent"
        private const val PING_PATH = "$PATH/ping"

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(token(EgressService.PENSJONSOPPTJENING).value)
            headers[Constants.NAV_CALL_ID] = MDC.get(Constants.NAV_CALL_ID)
        }
    }
}