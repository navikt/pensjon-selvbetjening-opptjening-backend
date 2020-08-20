package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

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
            return null;
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
            return null;
        }

        return responseEntity.getBody();
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
