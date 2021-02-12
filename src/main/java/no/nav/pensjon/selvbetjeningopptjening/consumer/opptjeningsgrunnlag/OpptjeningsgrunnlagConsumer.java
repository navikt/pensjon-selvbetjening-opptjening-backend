package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlagDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.InntektMapper;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;

@Component
public class OpptjeningsgrunnlagConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
    private static final String ENDPOINT_PATH = "/opptjeningsgrunnlag/";
    private static final String AUTH_TYPE = "Bearer";
    private final Log log = LogFactory.getLog(getClass());
    private final String endpoint;
    private final WebClient webClient;
    private final ServiceTokenGetter tokenGetter;

    public OpptjeningsgrunnlagConsumer(@Value("${popp.endpoint.url}") String endpoint,
                                       ServiceTokenGetter tokenGetter) {
        this.webClient = WebClient.create();
        this.endpoint = requireNonNull(endpoint);
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    public List<Inntekt> getInntektListeFromOpptjeningsgrunnlag(String fnr, Integer fomAr, Integer tomAr) {
        try {
            var response = webClient
                    .get()
                    .uri(opptjeningUri(fnr, fomAr, tomAr))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .retrieve()
                    .bodyToMono(HentOpptjeningsGrunnlagResponse.class)
                    .block();

            return response == null ? null : fromDto(response.getOpptjeningsGrunnlag());
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", CONSUMED_SERVICE, e.getMessage()), e);
            throw handle(e, CONSUMED_SERVICE);
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
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (StsException e) {
            log.error(String.format("STS error in %s: %s", CONSUMED_SERVICE, e.getMessage()), e);
            throw handle(e, CONSUMED_SERVICE);
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

    private String opptjeningUri(String fnr, Integer fomAr, Integer tomAr) {
        var builder = UriComponentsBuilder.fromHttpUrl(endpoint)
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
        return UriComponentsBuilder.fromHttpUrl(endpoint)
                .path(ENDPOINT_PATH + "ping")
                .toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }

    private static List<Inntekt> fromDto(OpptjeningsGrunnlagDto grunnlag) {
        return grunnlag == null ? null : InntektMapper.fromDto(grunnlag.getInntektListe());
    }
}
