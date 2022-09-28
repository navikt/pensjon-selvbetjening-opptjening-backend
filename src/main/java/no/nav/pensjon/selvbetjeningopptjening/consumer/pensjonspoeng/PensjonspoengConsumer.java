package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PensjonspoengMapper;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class PensjonspoengConsumer implements Pingable {

    private static final String SYSTEM = "POPP";
    private static final String CONSUMED_SERVICE = "PROPOPP019 hentPensjonspoengListe";
    private static final String PATH = "/popp/api";
    private static final String RESOURCE = "pensjonspoeng";
    private static final String PING_ACTION = "ping";
    private static final String PING_SERVICE = SYSTEM + " " + RESOURCE + " " + PING_ACTION;
    private static final String AUTH_TYPE = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(PensjonspoengConsumer.class);
    private final String url;
    private final WebClient webClient;
    private final ServiceTokenGetter tokenGetter;

    public PensjonspoengConsumer(@Qualifier("epoch-support") WebClient webClient,
                                 @Value("${popp.url}") String baseUrl,
                                 ServiceTokenGetter tokenGetter) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
        this.tokenGetter = requireNonNull(tokenGetter, "tokenGetter");
    }

    public List<Pensjonspoeng> getPensjonspoengListe(String fnr) {
        try {
            var response = webClient
                    .get()
                    .uri(buildUrl(fnr))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(NAV_CALL_ID, MDC.get(NAV_CALL_ID))
                    .retrieve()
                    .bodyToMono(PensjonspoengListeResponse.class)
                    .block();

            return response == null ? null : PensjonspoengMapper.fromDto(response.getPensjonspoeng());
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
            log.error(String.format("STS error in %s: %s", PING_SERVICE, e.getMessage()), e);
            throw handle(e, PING_SERVICE);
        } catch (WebClientResponseException e) {
            throw handle(e, PING_SERVICE);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw handle(e, PING_SERVICE);
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", SYSTEM + " " + RESOURCE, pingUri());
    }

    private String buildUrl(String fnr) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .pathSegment(RESOURCE, fnr)
                .toUriString();
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .pathSegment(RESOURCE, PING_ACTION)
                .toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }
}
