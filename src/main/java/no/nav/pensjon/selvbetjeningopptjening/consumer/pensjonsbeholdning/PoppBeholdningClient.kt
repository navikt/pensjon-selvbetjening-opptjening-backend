package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl.PoppBeholdningResult
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl.PoppBeholdningSpec
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.BeholdningClient
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
class PoppBeholdningClient(
    @Qualifier("epoch-support") private val webClient: WebClient,
    @Value($$"${popp.url}") private val baseUrl: String
) : BeholdningClient {

    private val log = KotlinLogging.logger { }

    override fun getPensjonsbeholdning(pid: Pid): List<Beholdning> {
        val uri = "$baseUrl/$PATH"
        log.debug { "POST to URL: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(PoppBeholdningSpec(pid.pid))
                .retrieve()
                .bodyToMono<PoppBeholdningResult>()
                .block()
                ?.beholdninger.orEmpty().map { it.fromDto() }
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
        PingInfo("REST", "POPP pensjonsbeholdning", "$baseUrl/$PING_PATH")

    private companion object {
        private const val CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe"
        private const val PATH = "popp/api/beholdning"
        private const val PING_PATH = "$PATH/ping"

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(token(EgressService.PENSJONSOPPTJENING).value)
            headers[Constants.NAV_CALL_ID] = MDC.get(Constants.NAV_CALL_ID)
        }
    }
}