package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.token.support.core.api.Unprotected;

import java.util.Optional;

@RestController
@RequestMapping("api/internal")
@Unprotected
public class ReadinessEndpoint {
    private OpptjeningProvider opptjeningProvider;

    public ReadinessEndpoint(OpptjeningProvider opptjeningProvider) {
        this.opptjeningProvider = opptjeningProvider;
    }

    @RequestMapping(path = "isAlive", method = RequestMethod.GET)
    public ResponseEntity isAlive() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "isReady", method = RequestMethod.GET)
    public ResponseEntity isReady() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "selvtest", method = RequestMethod.GET)
    public void ping() {
         opptjeningProvider.ping();
    }
}
