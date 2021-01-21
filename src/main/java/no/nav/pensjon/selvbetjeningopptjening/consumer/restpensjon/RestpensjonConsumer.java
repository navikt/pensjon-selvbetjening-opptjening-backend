package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.RestpensjonMapper.fromDto;

public class RestpensjonConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner";
    private final String endpoint;
    private RestTemplate restTemplate;
    //TODO use WebClient

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

            return response == null ? null : fromDto(response.getRestpensjoner());
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (RestClientException e) {
            throw handle(e, CONSUMED_SERVICE);
        }
    }

    @Override
    public void ping() {
        try {
            restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/restpensjon/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody();
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (RestClientException e) {
            throw handle(e, CONSUMED_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "POPP restpensjon", UriComponentsBuilder.fromHttpUrl(endpoint).path("/restpensjon/ping").toUriString());
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
