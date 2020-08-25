package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;

public class OpptjeningsgrunnlagConsumer {
    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;
    public static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
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
            throw handle(e);
        } catch(Exception e){
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
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

    private FailedCallingExternalServiceException handle(RestClientResponseException e) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "Received 401 UNAUTHORIZED", e);
        } else if (e.getRawStatusCode() == CHECKED_EXCEPTION_HTTP_STATUS && e.getMessage() != null && e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
            return new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "Person ikke funnet", e);
        } else if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        }

        return new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the provider", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
