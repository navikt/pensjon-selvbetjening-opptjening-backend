package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

@RestController
@RequestMapping("api")
@ProtectedWithClaims(issuer = ISSUER) // Use @Unprotected when running with laptop/uimage profile
public class OpptjeningEndpoint {

    private OpptjeningProvider provider;
    private StringExtractor fnrExtractor;

    public OpptjeningEndpoint(StringExtractor fnrExtractor) {
        this.fnrExtractor = fnrExtractor;
    }

    @GetMapping("/opptjening")
    public OpptjeningResponse getOpptjeningForFnr() {
        try {
            return provider.calculateOpptjeningForFnr(fnrExtractor.extract());
        } catch (FailedCallingExternalServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @Autowired
    public void setProvider(OpptjeningProvider provider) {
        this.provider = provider;
    }
}
