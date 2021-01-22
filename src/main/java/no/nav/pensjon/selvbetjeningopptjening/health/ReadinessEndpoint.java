package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/internal")
@Unprotected
public class ReadinessEndpoint {

    private final Selftest selftest;

    public ReadinessEndpoint(Selftest selftest) {
        this.selftest = selftest;
    }

    @RequestMapping(path = "isAlive", method = RequestMethod.GET)
    public ResponseEntity isAlive() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "isReady", method = RequestMethod.GET)
    public ResponseEntity isReady() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "selftest", method = RequestMethod.GET)
    public ResponseEntity selftest() {
        return new ResponseEntity<>(selftest.perform(), jsonContentType(), HttpStatus.OK);
    }

    private static MultiValueMap<String, String> jsonContentType() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
