package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.StsException;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

@Component
public class RestpensjonConsumer implements Pingable {

    private static final String SYSTEM = "POPP";
    private static final String CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner";
    private static final String RESOURCE = "restpensjon";
    private static final String PING_ACTION = "ping";
    private static final String PING_SERVICE = SYSTEM + " " + RESOURCE + " " + PING_ACTION;
    private static final String AUTH_TYPE = "Bearer";
    private final Log log = LogFactory.getLog(getClass());
    private final String endpoint;
    private final WebClient webClient;
    private final ServiceUserTokenGetter tokenGetter;

    RestpensjonConsumer(@Qualifier("epoch-support") WebClient webClient,
                        @Value("${popp.endpoint.url}") String endpoint,
                        ServiceUserTokenGetter tokenGetter) {
        this.webClient = requireNonNull(webClient);
        this.endpoint = requireNonNull(endpoint);
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    public List<Restpensjon> getRestpensjonListe(String fnr) {
        try {
            var response = webClient
                    .get()
                    .uri(buildUrl(fnr))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .retrieve()
                    .bodyToMono(RestpensjonListeResponse.class)
                    .block();

            return response == null ? null : fromDto(response.getRestpensjoner());
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
        return UriComponentsBuilder.fromHttpUrl(endpoint)
                .pathSegment(RESOURCE, fnr)
                .queryParam("hentSiste", "false")
                .toUriString();
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(endpoint)
                .pathSegment(RESOURCE, PING_ACTION)
                .toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }
}
