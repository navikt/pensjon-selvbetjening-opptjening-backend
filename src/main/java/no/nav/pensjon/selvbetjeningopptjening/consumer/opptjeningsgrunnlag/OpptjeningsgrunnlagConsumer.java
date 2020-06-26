package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingServiceInPoppException;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;

public class OpptjeningsgrunnlagConsumer {
    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;
    private final String endpoint;
    private RestTemplate restTemplate;

    public OpptjeningsgrunnlagConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Inntekt> getInntektListeFromOpptjeningsgrunnlag(String fnr, Integer fomAr, Integer tomAr) {
        ResponseEntity<HentOpptjeningsGrunnlagResponse> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    buildUrl("/opptjeningsgrunnlag/" + fnr, fomAr, tomAr),
                    HttpMethod.GET,
                    null,
                    HentOpptjeningsGrunnlagResponse.class);
        } catch (RestClientResponseException e) {
            return handle(e);
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getOpptjeningsGrunnlag().getInntektListe() : null;
    }

    private String buildUrl(String path, Integer fomAr, Integer tomAr) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path(path);

        if (fomAr != null) {
            builder.queryParam("fomAr", Integer.toString(fomAr));
        }

        if (tomAr != null) {
            builder.queryParam("tomAr", Integer.toString(tomAr));
        }

        return builder.toUriString();
    }

    private List<Inntekt> handle(RestClientResponseException e) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            throw new FailedCallingServiceInPoppException("Received 401 UNAUTHORIZED from PROPOPP007 hentOpptjeningsgrunnlag", e);
        } else if (e.getRawStatusCode() == CHECKED_EXCEPTION_HTTP_STATUS && e.getMessage() != null && e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
            throw new FailedCallingServiceInPoppException("Person ikke funnet i POPP when calling PROPOPP007 hentOpptjeningsgrunnlag", e);
        }
        throw new FailedCallingServiceInPoppException("An error occurred when calling PROPOPP007 hentOpptjeningsgrunnlag", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
