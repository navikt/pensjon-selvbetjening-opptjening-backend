package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl.AarvisBeholdningOgMerknaderV1
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknaderService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1")
class BeholdningOgMerknaderControllerV1(
    private val service: BeholdningOgMerknaderService,
    private val pidGetter: TargetPidExtractor
) {
    private val log = KotlinLogging.logger { }

    @GetMapping("/beholdninger-og-merknader")
    fun beholdningerOgMerknader(): AarvisBeholdningOgMerknaderV1 {
        try {
            return AarvisBeholdningOgMerknaderV1.transferable(service.beholdningOgMerknader(pidGetter.pid()))
        } catch (e: PidValidationException) {
            log.error(e) { "PID validation failed" }
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: FailedCallingExternalServiceException) {
            log.error(e) { "External service call failed" }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
}