package no.nav.pensjon.selvbetjeningopptjening.logging;

import no.nav.security.token.support.core.api.Unprotected;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Unprotected
@RestController
@RequestMapping("api")
public class LoggingEndpoint {
    private static final Log log = LogFactory.getLog(LoggingEndpoint.class);

    @PutMapping(path = "logg")
    public ResponseEntity<Object> logg(@RequestBody LogMessage logger) {
        if("info".equalsIgnoreCase(logger.getType())) {
            log.info(logger.getJsonContent());
        } else if("error".equalsIgnoreCase(logger.getType())) {
            log.error(logger.getJsonContent());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
