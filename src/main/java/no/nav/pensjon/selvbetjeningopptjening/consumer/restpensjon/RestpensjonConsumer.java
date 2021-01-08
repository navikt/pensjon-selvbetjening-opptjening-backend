package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.common.selftest.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.common.selftest.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.RestpensjonMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class RestpensjonConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner";
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

            return response == null ? null : fromDto(response.getRestpensjoner());
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
        }
    }

    @Override
    public Optional<String> ping() {
        try {
            return Optional.ofNullable(restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/restpensjon/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody());
        } catch (RestClientResponseException rce) {
            throw handle(rce, CONSUMED_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "POPP Rest Pensjon", UriComponentsBuilder.fromHttpUrl(endpoint).path("/restpensjon/ping").toUriString());
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
