package no.nav.pensjon.selvbetjeningopptjening.opptjening.api

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningProvider
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class OpptjeningController(
    private val provider: OpptjeningProvider,
    private val pidGetter: TargetPidExtractor
) {
    private val log = KotlinLogging.logger { }

    @GetMapping("/opptjening")
    fun getOpptjening(): OpptjeningResponse? {
        try {
            return provider.calculateOpptjeningForFnr(pidGetter.pid())
        } catch (e: PidValidationException) {
            log.error(e) { "PID validation failed" }
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: FailedCallingExternalServiceException) {
            log.error(e) { "External service call failed" }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
}
