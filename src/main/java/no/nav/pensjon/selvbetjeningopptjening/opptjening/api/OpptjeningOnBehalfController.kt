package no.nav.pensjon.selvbetjeningopptjening.opptjening.api

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningProvider
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse
import no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Deprecated("not allowed to use PID as request parameter")
@RestController
@RequestMapping("api")
class OpptjeningOnBehalfController(
    private val provider: OpptjeningProvider,
    private val pidGetter: TargetPidExtractor
) {
    private val log = KotlinLogging.logger { }

    @GetMapping("/opptjeningonbehalf")
    fun getOpptjeningOnBehalfOf(@RequestParam(value = "pid") pidValue: String?): OpptjeningResponse? {
        log.info("Received on-behalf-of request for opptjening for pid {}", Masker.maskFnr(pidValue))

        try {
            // Audit takes place in ImpersonalAccessFilter
            return provider.calculateOpptjeningForFnr(pidGetter.pid())
        } catch (e: PidValidationException) {
            log.error("Invalid PID: {}", Masker.maskFnr(pidValue), e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: FailedCallingExternalServiceException) {
            log.error("Failed calling external service", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
}
