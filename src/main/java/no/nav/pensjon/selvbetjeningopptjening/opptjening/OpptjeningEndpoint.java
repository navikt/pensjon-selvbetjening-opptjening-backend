package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingServiceInPoppException;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.util.FnrExtractor;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@RestController
@RequestMapping("api/opptjening")
//@ProtectedWithClaims(issuer = ISSUER, claimMap = { "acr=Level4" })
@ProtectedWithClaims(issuer = ISSUER)
public class OpptjeningEndpoint {

    private OpptjeningProvider provider;
    private PensjonspoengConsumer pensjonspoengConsumer;
    private FnrExtractor fnrExtractor;

    public OpptjeningEndpoint(FnrExtractor fnrExtractor) {
        this.fnrExtractor = fnrExtractor;
    }

    @GetMapping("/pensjonspoeng")
    public List<Pensjonspoeng> getPensjonspoeng() {
        return pensjonspoengConsumer.getPensjonspoengListe(fnrExtractor.extract());
    }

    @GetMapping("/opptjening")
    public OpptjeningResponse getOpptjeningForFnr() {
        try {
            return provider.calculateOpptjeningForFnr(fnrExtractor.extract());
        } catch (FailedCallingServiceInPoppException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @Autowired
    public void setPensjonspoengConsumer(PensjonspoengConsumer pensjonspoengConsumer) {
        this.pensjonspoengConsumer = pensjonspoengConsumer;
    }

    @Autowired
    public void setProvider(OpptjeningProvider provider) {
        this.provider = provider;
    }
}
