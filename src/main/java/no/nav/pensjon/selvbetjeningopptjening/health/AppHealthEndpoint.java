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
@RequestMapping("internal")
@Unprotected
public class AppHealthEndpoint {

    @RequestMapping(path = "alive", method = RequestMethod.GET)
    public ResponseEntity<?> isAlive() {
        return statusOk();
    }

    @RequestMapping(path = "ready", method = RequestMethod.GET)
    public ResponseEntity<?> isReady() {
        return statusOk();
    }

    /**
     * Ref. Team Digital Status: Statusplattformen: Metoder for å oppdatere status
     * (https://confluence.adeo.no/pages/viewpage.action?pageId=460442120)
     */
    private static ResponseEntity<?> statusOk() {
        return new ResponseEntity<>(
                "{\"status\":\"OK\"}",
                jsonContentHeader(),
                HttpStatus.OK);
    }

    private static MultiValueMap<String, String> jsonContentHeader() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}