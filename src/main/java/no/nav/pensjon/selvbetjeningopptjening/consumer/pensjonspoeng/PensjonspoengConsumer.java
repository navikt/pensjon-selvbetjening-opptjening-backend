package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingServiceInPoppException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.model.Pensjonspoeng;

public class PensjonspoengConsumer {
    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;
    private final String endpoint;
    private RestTemplate restTemplate;

    public PensjonspoengConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Pensjonspoeng> hentPensjonspoengListe(String fnr) {
        ResponseEntity<HentPensjonspoengListeResponse> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            responseEntity = restTemplate.exchange(
                    buildUrl(fnr),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    HentPensjonspoengListeResponse.class);
        } catch (RestClientResponseException e) {
            return handle(e);
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getPensjonspoeng() : null;
    }

    private String buildUrl(String fnr) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/" + fnr);

        return builder.toUriString();
    }

    private List<Pensjonspoeng> handle(RestClientResponseException e) {
        if (e.getRawStatusCode() == 401) {
            throw new FailedCallingServiceInPoppException("Received 401 UNAUTHORIZED from PROPOPP019 hentPensjonspoengListe", e);
        } else if (e.getRawStatusCode() == CHECKED_EXCEPTION_HTTP_STATUS && e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
            throw new FailedCallingServiceInPoppException("Person ikke funnet i POPP when calling PROPOPP19 hentPensjonspoengListe", e);
        }
        throw new FailedCallingServiceInPoppException("An error occurred when calling PROPOPP019 hentPensjonspoengListe", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
