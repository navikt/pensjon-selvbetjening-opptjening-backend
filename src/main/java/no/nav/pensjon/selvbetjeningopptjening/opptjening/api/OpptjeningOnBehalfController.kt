package no.nav.pensjon.selvbetjeningopptjening.opptjening.api

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningProvider
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.masking.Masker
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
    private val auditor: Auditor,
    private val pidGetter: TargetPidExtractor
) {
    private val log = KotlinLogging.logger { }

    @GetMapping("/opptjeningonbehalf")
    fun getOpptjeningOnBehalfOf(@RequestParam(value = "pid") pidValue: String?, request: HttpServletRequest): OpptjeningResponse? {

        if (pidValue?.contains(ENCRYPTION_MARK) == false ) {
            log.info("Using unencrypted PID in request :-(")
        }
        val pidValueFromSecurityContextPidExtractor = pidGetter.pid().pid
        val maskedPid = pidValueFromSecurityContextPidExtractor?.let(Masker::maskFnr)
        log.info("Received on-behalf-of request for opptjening for pid {}", maskedPid)

        try {
            val pid = Pid(pidValueFromSecurityContextPidExtractor)
            auditor.audit(onBehalfOfPid = pid, requestUri = request.requestURI)
            return provider.calculateOpptjeningForFnr(pid)
        } catch (e: PidValidationException) {
            log.error("Invalid PID: {}", maskedPid, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: FailedCallingExternalServiceException) {
            log.error("Failed calling external service", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
    private companion object {
        const val ENCRYPTION_MARK = "."
    }
}
