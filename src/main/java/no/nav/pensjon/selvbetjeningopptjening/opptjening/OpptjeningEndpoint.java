package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/opptjening")
public class OpptjeningEndpoint {

    @GetMapping("/{fnr}")
    public Opptjening getOpptjening(@PathVariable String fnr) {
        return new Opptjening(fnr, 123000);
    }
}
