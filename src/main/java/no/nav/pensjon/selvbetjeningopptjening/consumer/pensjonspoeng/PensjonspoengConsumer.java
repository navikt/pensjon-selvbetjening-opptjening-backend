package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class PensjonspoengConsumer {

    static final String CONSUMED_SERVICE = "PROPOPP019 hentPensjonspoengListe";
    private final String endpoint;
    private RestTemplate restTemplate;

    public PensjonspoengConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Pensjonspoeng> getPensjonspoengListe(String fnr) {
        try {
            PensjonspoengListeResponse response = restTemplate.exchange(
                    buildUrl(fnr),
                    HttpMethod.GET,
                    null,
                    PensjonspoengListeResponse.class)
                    .getBody();

            return response == null ? null : response.getPensjonspoeng();
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
        }
    }

    private String buildUrl(String fnr) {
        return UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path("/pensjonspoeng/" + fnr)
                .toUriString();
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
