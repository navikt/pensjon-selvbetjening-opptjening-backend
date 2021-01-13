package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import no.nav.pensjon.selvbetjeningopptjening.common.selftest.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.common.selftest.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.BeholdningMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class PensjonsbeholdningConsumer implements Pingable {

    static final String CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe";
    private final String endpoint;
    private RestTemplate restTemplate;

    public PensjonsbeholdningConsumer(String endpoint, RestTemplate restTemplate) {
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
    }

    public List<Beholdning> getPensjonsbeholdning(String fnr) {
        try {
            BeholdningListeResponse response = restTemplate.exchange(
                    buildUrl(),
                    HttpMethod.POST,
                    newRequestEntity(fnr),
                    BeholdningListeResponse.class)
                    .getBody();

            return response == null ? null : fromDto(response.getBeholdninger());
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

    private static HttpEntity<BeholdningListeRequest> newRequestEntity(String fnr) {
        return new HttpEntity<>(
                new BeholdningListeRequest(fnr),
                new HttpHeaders());
    }

    @Override
    public void ping() {
        try {
                restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/beholdning/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody();
        } catch (RestClientResponseException rce) {
            throw handle(rce, CONSUMED_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "POPP Pensjon Beholdning", buildUrl());
    }
}
