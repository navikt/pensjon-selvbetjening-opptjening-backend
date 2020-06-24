package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingServiceInPoppException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.model.Restpensjon;

public class RestpensjonConsumer {
    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;
    private final String endpoint;
    private RestTemplate restTemplate;

    public RestpensjonConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Restpensjon> hentRestpensjonListe(String fnr) {
        ResponseEntity<HentRestpensjonListeResponse> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            responseEntity = restTemplate.exchange(
                    buildUrl(fnr),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    HentRestpensjonListeResponse.class);
        } catch (RestClientResponseException e) {
            return handle(e);
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getRestpensjoner() : null;
    }

    private String buildUrl(String fnr)  {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/")
                .path(fnr)
                .queryParam("hentSiste", "false");

        return builder.toUriString();
    }

    private List<Restpensjon> handle(RestClientResponseException e) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            throw new FailedCallingServiceInPoppException("Received unauthorized from PROPOPP013 hentRestpensjoner", e);
        }

        if (e.getRawStatusCode() == CHECKED_EXCEPTION_HTTP_STATUS && e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
            throw new FailedCallingServiceInPoppException("Person not found in POPP when calling PROPOPP13 hentRestpensjoner", e);
        }

        throw new FailedCallingServiceInPoppException("Unexpected error while trying to call PROPOPP13 hentRestpensjoner", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
