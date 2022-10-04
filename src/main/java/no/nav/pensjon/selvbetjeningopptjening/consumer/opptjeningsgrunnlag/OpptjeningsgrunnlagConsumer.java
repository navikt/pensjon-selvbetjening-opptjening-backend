package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlagDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.InntektMapper;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.consumer.PoppUtil.handle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;

@Component
public class OpptjeningsgrunnlagConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
    private static final String PATH = "/popp/api";
    private static final String SUB_PATH = "/opptjeningsgrunnlag/";
    private static final String AUTH_TYPE = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(OpptjeningsgrunnlagConsumer.class);
    private final String url;
    private final WebClient webClient;
    private final TokenGetterFacade tokenGetter;

    public OpptjeningsgrunnlagConsumer(@Value("${popp.url}") String baseUrl,
                                       TokenGetterFacade tokenGetter) {
        this.webClient = WebClient.create();
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
        this.tokenGetter = requireNonNull(tokenGetter, "tokenGetter");
    }

    public List<Inntekt> getInntektListeFromOpptjeningsgrunnlag(String fnr, Integer fomAr, Integer tomAr) {
        try {
            var response = webClient
                    .get()
                    .uri(opptjeningUri(fnr, fomAr, tomAr))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(NAV_CALL_ID, MDC.get(NAV_CALL_ID))
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
                    .header(NAV_CALL_ID, MDC.get(NAV_CALL_ID))
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
        var builder = UriComponentsBuilder.fromHttpUrl(url)
                .path(SUB_PATH + fnr);

        if (fomAr != null) {
            builder.queryParam("fomAr", Integer.toString(fomAr));
        }

        if (tomAr != null) {
            builder.queryParam("tomAr", Integer.toString(tomAr));
        }

        return builder.toUriString();
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .path(SUB_PATH + "ping")
                .toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getToken(AppIds.PENSJONSOPPTJENING_REGISTER.appName);
    }

    private static List<Inntekt> fromDto(OpptjeningsGrunnlagDto grunnlag) {
        return grunnlag == null ? null : InntektMapper.fromDto(grunnlag.getInntektListe());
    }
}
