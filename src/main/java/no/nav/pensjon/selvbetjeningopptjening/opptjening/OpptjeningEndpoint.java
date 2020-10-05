package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.unleash.UnleashProvider.toggle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import no.nav.pensjon.selvbetjeningopptjening.config.OpptjeningFeature;
import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@RestController
@RequestMapping("api")
@ProtectedWithClaims(issuer = ISSUER) // Use @Unprotected when running with laptop/uimage profile
public class OpptjeningEndpoint {

    private final Log log = LogFactory.getLog(getClass());
    private OpptjeningProvider provider;
    private StringExtractor fnrExtractor;

    public OpptjeningEndpoint(OpptjeningProvider provider, StringExtractor fnrExtractor) {
        this.provider = provider;
        this.fnrExtractor = fnrExtractor;
    }

    @GetMapping("/opptjening")
    public OpptjeningResponse getOpptjeningForFnr() {
        try {
            if (toggle(OpptjeningFeature.PEN_PL1441).isEnabled()) {
                return provider.calculateOpptjeningForFnr(fnrExtractor.extract());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The service is not made available for the specified user yet");
            }
        } catch (FailedCallingExternalServiceException e) {
            log.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
