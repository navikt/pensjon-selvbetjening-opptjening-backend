package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class PensjonsbeholdningConsumer {

    static final String CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe";
    private final String endpoint;
    private RestTemplate restTemplate;

    public PensjonsbeholdningConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Beholdning> getPensjonsbeholdning(String fnr) {
        try {
            BeholdningListeResponse response = restTemplate.exchange(
                    buildUrl(),
                    HttpMethod.POST,
                    newRequestEntity(fnr),
                    BeholdningListeResponse.class)
                    .getBody();

            return response == null ? null : response.getBeholdninger();
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
        }
    }

    private String buildUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/beholdning")
                .toUriString();
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static HttpEntity<BeholdningListeRequest> newRequestEntity(String fnr) {
        return new HttpEntity<>(
                new BeholdningListeRequest(fnr),
                new HttpHeaders());
    }
}
