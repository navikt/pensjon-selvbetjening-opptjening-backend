package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1.acl.MerknaderV1
import no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1.acl.MerknadMapper.transferable
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import no.nav.pensjon.selvbetjeningopptjening.merknad.MerknadService
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class MerknadController(
    private val service: MerknadService,
    private val pidGetter: TargetPidExtractor
) {
    private val log = KotlinLogging.logger { }

    @GetMapping("/merknader")
    fun getMerknader(): MerknaderV1 {
        try {
            return transferable(service.merknaderPerAar(pidGetter.pid()))
        } catch (e: PidValidationException) {
            log.error(e) { "PID validation failed" }
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: FailedCallingExternalServiceException) {
            log.error(e) { "External service call failed" }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
}