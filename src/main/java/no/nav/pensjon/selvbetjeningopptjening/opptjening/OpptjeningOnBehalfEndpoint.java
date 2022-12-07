package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.audit.CefEntry;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

@RestController
@RequestMapping("api")
public class OpptjeningOnBehalfEndpoint {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final Logger log = LoggerFactory.getLogger(OpptjeningOnBehalfEndpoint.class);
    private final OpptjeningProvider provider;

    public OpptjeningOnBehalfEndpoint(OpptjeningProvider provider) {
        this.provider = requireNonNull(provider);
    }

    @GetMapping("/opptjeningonbehalf")
    public OpptjeningResponse getOpptjeningOnBehalfOf(@RequestParam(value = "fnr") String pidValue) {
        log.info("Received on-behalf-of request for opptjening for fnr {}", maskFnr(pidValue));

        try {
            audit.info(getAuditInfo(pidValue, RequestContext.getNavIdent()).format());
            return provider.calculateOpptjeningForFnr(new Pid(RequestContext.getSubjectPid()));
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        } catch (JwtException e) {
            log.error("JWT-related error", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        } catch (PidValidationException e) {
            log.error("Invalid PID: {}", maskFnr(pidValue), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (FailedCallingExternalServiceException e) {
            log.error("Failed calling external service", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private static CefEntry getAuditInfo(String pid, String navIdent) {
        log.info("NAV-ident: {}", navIdent == null ? "null" : navIdent.charAt(0) + "*****");

        return new CefEntry(
                ZonedDateTime.now().toInstant().toEpochMilli(),
                Level.INFO,
                "audit:access",
                "Datahenting paa vegne av",
                "Internbruker henter pensjonsopptjeningsdata for innbygger",
                navIdent,
                pid);
    }
}
