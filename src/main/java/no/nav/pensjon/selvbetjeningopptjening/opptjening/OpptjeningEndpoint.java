package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.pensjon.selvbetjeningopptjening.consumers.HentPensjonspoengListeRequest;
import no.nav.pensjon.selvbetjeningopptjening.consumers.HentPensjonspoengListeResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumers.PensjonspoengConsumer;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@RestController
@RequestMapping("api/opptjening")
//@ProtectedWithClaims(issuer = ISSUER, claimMap = { "acr=Level4" })
@ProtectedWithClaims(issuer = ISSUER)
public class OpptjeningEndpoint {
    private PensjonspoengConsumer pensjonspoengConsumer;

    @GetMapping("/{fnr}")
    public Opptjening getOpptjening(@PathVariable String fnr) {
        return new Opptjening(fnr, 123000);
    }

    @GetMapping("/pensjonspoeng/{fnr}")
    public HentPensjonspoengListeResponse getPensjonspoeng(@PathVariable String fnr) {
        HentPensjonspoengListeRequest request = new HentPensjonspoengListeRequest(fnr);
        return pensjonspoengConsumer.hentPensjonspoengListe(request);
    }

    @Autowired
    public void setPensjonspoengConsumer(PensjonspoengConsumer pensjonspoengConsumer) {
        this.pensjonspoengConsumer = pensjonspoengConsumer;
    }
}
