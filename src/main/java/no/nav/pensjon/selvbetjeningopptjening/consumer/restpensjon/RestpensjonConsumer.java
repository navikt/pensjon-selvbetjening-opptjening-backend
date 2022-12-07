package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
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
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.RestpensjonMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;

@Component
public class RestpensjonConsumer implements Pingable {

    private static final String SYSTEM = "POPP";
    private static final String CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner";
    private static final String PATH = "/popp/api";
    private static final String RESOURCE = "restpensjon";
    private static final String PING_ACTION = "ping";
    private static final String PING_SERVICE = SYSTEM + " " + RESOURCE + " " + PING_ACTION;
    private static final String AUTH_TYPE = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(RestpensjonConsumer.class);
    private final String url;
    private final WebClient webClient;

    RestpensjonConsumer(@Qualifier("epoch-support") WebClient webClient,
                        @Value("${popp.url}") String baseUrl) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
    }

    public List<Restpensjon> getRestpensjonListe(String fnr) {
        if (log.isDebugEnabled()) {
            log.debug("Calling {} for PID {}", CONSUMED_SERVICE, maskFnr(fnr));
        }

        try {
            var response = webClient
                    .get()
                    .uri(buildUrl(fnr))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(NAV_CALL_ID, MDC.get(NAV_CALL_ID))
                    .retrieve()
                    .bodyToMono(RestpensjonListeResponse.class)
                    .block();

            return response == null ? null : fromDto(response.getRestpensjoner());
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
                .queryParam("hentSiste", "false")
                .toUriString();
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .pathSegment(RESOURCE, PING_ACTION)
                .toUriString();
    }

    private String getAuthHeaderValue() {
        return AUTH_TYPE + " " + RequestContext.getEgressAccessToken(AppIds.PENSJONSOPPTJENING_REGISTER).getValue();
    }
}
