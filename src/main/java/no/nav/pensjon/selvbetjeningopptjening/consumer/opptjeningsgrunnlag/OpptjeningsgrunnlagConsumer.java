package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlagDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.InntektMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;

public class OpptjeningsgrunnlagConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
    private static final String ENDPOINT_PATH = "/opptjeningsgrunnlag/";
    private final String endpoint;
    private final WebClient webClient;

    public OpptjeningsgrunnlagConsumer(String endpoint) {
        this.webClient = WebClient.create();
        this.endpoint = endpoint;
    }

    public List<Inntekt> getInntektListeFromOpptjeningsgrunnlag(String fnr, Integer fomAr, Integer tomAr) {
        try {
            var response = webClient
                    .get()
                    .uri(buildUrl(fnr, fomAr, tomAr))
                    .retrieve()
                    .bodyToMono(HentOpptjeningsGrunnlagResponse.class)
                    .block();

            return response == null ? null : fromDto(response.getOpptjeningsGrunnlag());
        } catch (WebClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, CONSUMED_SERVICE);
        }
    }

    @Override
    public void ping() {
        try {
            webClient
                    .get()
                    .uri(pingUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw handle(e, CONSUMED_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, CONSUMED_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "POPP opptjeningsgrunnlag", pingUri());
    }

    private String buildUrl(String fnr, Integer fomAr, Integer tomAr) {
        var builder = UriComponentsBuilder
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

    private String pingUri() {
        return UriComponentsBuilder
                .fromHttpUrl(endpoint)
                .path(ENDPOINT_PATH + "ping")
                .toUriString();
    }

    private static List<Inntekt> fromDto(OpptjeningsGrunnlagDto grunnlag) {
        return grunnlag == null ? null : InntektMapper.fromDto(grunnlag.getInntektListe());
    }
}
