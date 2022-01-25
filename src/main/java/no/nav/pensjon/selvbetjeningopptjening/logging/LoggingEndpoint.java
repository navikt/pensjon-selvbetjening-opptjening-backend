package no.nav.pensjon.selvbetjeningopptjening.logging;

import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

@RestController
@RequestMapping("api")
@ProtectedWithClaims(issuer = ISSUER)
public class LoggingEndpoint {
    private static final Logger log = LoggerFactory.getLogger(LoggingEndpoint.class);
    @PutMapping(path = "logg")
    public ResponseEntity<Object> logg(@RequestBody LogMessage logger) {
        if("info".equalsIgnoreCase(logger.getType())) {
            log.info("info {}", logger.getJsonContent());
        } else if("error".equalsIgnoreCase(logger.getType())) {
            log.error("error {}", logger.getJsonContent());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
