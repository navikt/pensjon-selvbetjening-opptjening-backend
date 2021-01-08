package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import no.nav.pensjon.selvbetjeningopptjening.common.selftest.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.common.selftest.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.InntektMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class OpptjeningsgrunnlagConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
    private static final String ENDPOINT_PATH = "/opptjeningsgrunnlag/";
    private final String endpoint;
    private RestTemplate restTemplate;
    //TODO Migrate to WebClient

    public OpptjeningsgrunnlagConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Inntekt> getInntektListeFromOpptjeningsgrunnlag(String fnr, Integer fomAr, Integer tomAr) {
        try {
            HentOpptjeningsGrunnlagResponse response = restTemplate.exchange(
                    buildUrl(fnr, fomAr, tomAr),
                    HttpMethod.GET,
                    null,
                    HentOpptjeningsGrunnlagResponse.class)
                    .getBody();

            return response == null ? null : fromDto(response.getOpptjeningsGrunnlag().getInntektListe());
        } catch (RestClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(POPP, CONSUMED_SERVICE, "An error occurred in the consumer", e);
        }
    }

    private String buildUrl(String fnr, Integer fomAr, Integer tomAr) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path(ENDPOINT_PATH + fnr);

        if (fomAr != null) {
            builder.queryParam("fomAr", Integer.toString(fomAr));
        }

        if (tomAr != null) {
            builder.queryParam("tomAr", Integer.toString(tomAr));
        }

        return builder.toUriString();
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<String> ping() {
        try {
            return Optional.ofNullable(restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/opptjeningsgrunnlag/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody());
        } catch (RestClientResponseException rce) {
            throw handle(rce, CONSUMED_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "POPP Pensjon Beholdning", UriComponentsBuilder.fromHttpUrl(endpoint).path("/opptjeningsgrunnlag/ping").toUriString());
    }

}
