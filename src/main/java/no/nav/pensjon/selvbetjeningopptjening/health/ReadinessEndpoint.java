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

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity selftest(HttpServletRequest request) {
        String accept = request.getHeader(HttpHeaders.ACCEPT);

        return MediaType.APPLICATION_JSON_VALUE.equals(accept)
                ? responseEntity(selftest.performJson(), MediaType.APPLICATION_JSON_VALUE)
                : responseEntity(selftest.performHtml(), MediaType.TEXT_HTML_VALUE);
    }

    private static ResponseEntity<String> responseEntity(String body, String mediaType) {
        return new ResponseEntity<>(
                body,
                contentTypeHeaders(mediaType),
                HttpStatus.OK);
    }

    private static MultiValueMap<String, String> contentTypeHeaders(String mediaType) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, mediaType);
        return headers;
    }
}
