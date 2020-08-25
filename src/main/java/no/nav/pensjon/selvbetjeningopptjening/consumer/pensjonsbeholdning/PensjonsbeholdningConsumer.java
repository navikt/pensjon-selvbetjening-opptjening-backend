package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

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

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;

public class PensjonsbeholdningConsumer {
    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;
    public static final String CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe";
    private final String endpoint;
    private RestTemplate restTemplate;

    public PensjonsbeholdningConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Beholdning> getPensjonsbeholdning(String fnr) {
        ResponseEntity<BeholdningListeResponse> responseEntity;
        BeholdningListeRequest request = new BeholdningListeRequest(fnr);
        try {
            HttpHeaders headers = new HttpHeaders();
            responseEntity = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/beholdning").toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    BeholdningListeResponse.class);
        } catch (RestClientResponseException e) {
            throw handle(e);
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
        }

        return responseEntity.getBody() != null ? responseEntity.getBody().getBeholdninger() : null;
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
