package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api")
public class OpptjeningEndpoint {

    private static final Logger log = LoggerFactory.getLogger(OpptjeningEndpoint.class);
    private final OpptjeningProvider provider;

    public OpptjeningEndpoint(OpptjeningProvider provider) {
        this.provider = provider;
    }

    @GetMapping("/opptjening")
    public OpptjeningResponse getOpptjening() {
        try {
            return provider.calculateOpptjeningForFnr(new Pid(RequestContext.getSubjectPid()));
        } catch (PidValidationException e) {
            log.error("PID validation failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (FailedCallingExternalServiceException e) {
            log.error("External service call failed", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
