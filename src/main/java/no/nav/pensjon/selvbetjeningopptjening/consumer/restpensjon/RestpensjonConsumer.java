package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class RestpensjonConsumer {

    static final String CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner";
    private final String endpoint;
    private RestTemplate restTemplate;

    public RestpensjonConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Restpensjon> getRestpensjonListe(String fnr) {
        try {
            RestpensjonListeResponse response = restTemplate.exchange(
                    buildUrl(fnr),
                    HttpMethod.GET,
                    null,
                    RestpensjonListeResponse.class)
                    .getBody();

            return response == null ? null : response.getRestpensjoner();
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
        }
    }

    private String buildUrl(String fnr) {
        return UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/restpensjon/")
                .path(fnr)
                .queryParam("hentSiste", "false")
                .toUriString();
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
