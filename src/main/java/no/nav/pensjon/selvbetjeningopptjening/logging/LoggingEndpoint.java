package no.nav.pensjon.selvbetjeningopptjening.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class LoggingEndpoint {

    private static final Logger log = LoggerFactory.getLogger(LoggingEndpoint.class);

    @PutMapping(path = "logg")
    public ResponseEntity<Object> log(@RequestBody LogMessage message) {
        String type = message.getType();

        if ("info".equalsIgnoreCase(type)) {
            log.info("info {}", message.getJsonContent());
        } else if ("error".equalsIgnoreCase(type)) {
            log.error("error {}", message.getJsonContent());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
