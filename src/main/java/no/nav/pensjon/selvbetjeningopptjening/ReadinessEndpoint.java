package no.nav.pensjon.selvbetjeningopptjening;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/internal")
public class ReadinessEndpoint {
    @RequestMapping(path = "isAlive", method = RequestMethod.GET)
    public ResponseEntity isAlive() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "isReady", method = RequestMethod.GET)
    public ResponseEntity isReady() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
