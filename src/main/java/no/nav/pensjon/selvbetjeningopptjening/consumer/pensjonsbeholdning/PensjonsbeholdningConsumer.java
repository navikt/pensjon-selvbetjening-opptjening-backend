package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;

public class PensjonsbeholdningConsumer {
    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;
    private final String endpoint;
    private RestTemplate restTemplate;

    public PensjonsbeholdningConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<Integer, Beholdning> getPensjonsbeholdningMap(String fnr) {
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
            return handle(e);
        }

        return responseEntity.getBody() != null ? createBeholdningMap(responseEntity.getBody().getBeholdninger()) : null;
    }

    private Map<Integer, Beholdning> handle(RestClientResponseException e) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            throw new FailedCallingServiceInPoppException("Received 401 UNAUTHORIZED from PROPOPP006 hentPensjonsbeholdningListe", e);
        } else if (e.getRawStatusCode() == CHECKED_EXCEPTION_HTTP_STATUS  && e.getMessage() != null && e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
            throw new FailedCallingServiceInPoppException("Person ikke funnet i POPP when calling PROPOPP006 hentPensjonsbeholdningListe", e);
        }
        throw new FailedCallingServiceInPoppException("An error occurred when calling PROPOPP006 hentPensjonsbeholdningListe", e);
    }

    private Map<Integer, Beholdning> createBeholdningMap(List<Beholdning> beholdningList) {
        Map<Integer, Beholdning> beholdningMap = new HashMap<>();
        if (beholdningList != null) {
            for (Beholdning beholdning : beholdningList) {
                if (beholdning.getFomDato() != null) {
                    beholdningMap.put(beholdning.getFomDato().getYear(), beholdning);
                }
            }
        }
        return beholdningMap;
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
