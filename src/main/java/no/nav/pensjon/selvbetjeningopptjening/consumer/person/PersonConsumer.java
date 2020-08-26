package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;

public class PersonConsumer {
    private String endpoint;
    private RestTemplate restTemplate;

    public PersonConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public AfpHistorikk getAfpHistorikkForPerson(String fnr) {
        ResponseEntity<AfpHistorikk> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("pid", fnr);
            responseEntity = restTemplate.exchange(
                    endpoint + "/person/afphistorikk",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    AfpHistorikk.class);
        } catch (RestClientResponseException e) {
            throw handle(e, "PROPEN2602 getAfphistorikkForPerson");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN2602 getAfphistorikkForPerson", "An error occurred in the consumer", e);
        }

        return responseEntity.getBody();
    }

    public UforeHistorikk getUforeHistorikkForPerson(String fnr) {
        ResponseEntity<UforeHistorikk> responseEntity;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("pid", fnr);
            responseEntity = restTemplate.exchange(
                    endpoint + "/person/uforehistorikk",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UforeHistorikk.class);
        } catch (RestClientResponseException e) {
            throw handle(e, "PROPEN2603 getUforehistorikkForPerson");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN2603 getUforehistorikkForPerson", "An error occurred in the consumer", e);
        }

        return responseEntity.getBody();
    }

    private FailedCallingExternalServiceException handle(RestClientResponseException e, String serviceIdentifier) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 401 UNAUTHORIZED", e);
        } else if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        } else if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 400 BAD REQUEST", e);
        }
        return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the consumer", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
