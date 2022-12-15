package no.nav.pensjon.selvbetjeningopptjening.health;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal")
public class AppHealthEndpoint {

    @RequestMapping(path = "alive", method = RequestMethod.GET)
    public ResponseEntity<?> isAlive() {
        return statusUp();
    }

    @RequestMapping(path = "ready", method = RequestMethod.GET)
    public ResponseEntity<?> isReady() {
        return statusUp();
    }

    private static ResponseEntity<?> statusUp() {
        return new ResponseEntity<>(
                "up",
                plainTextContentHeader(),
                HttpStatus.OK);
    }

    private static MultiValueMap<String, String> plainTextContentHeader() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return headers;
    }
}
