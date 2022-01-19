package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.BeholdningMapper.fromDto;

@Component
public class PensjonsbeholdningConsumer implements Pingable {

    private static final String CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe";
    private static final String PATH = "/popp/api";
    private static final String SUB_PATH = "/beholdning";
    private static final String AUTH_TYPE = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(PensjonsbeholdningConsumer.class);
    private final String url;
    private final WebClient webClient;
    private final ServiceTokenGetter tokenGetter;

    public PensjonsbeholdningConsumer(@Qualifier("epoch-support") WebClient webClient,
                                      @Value("${popp.url}") String baseUrl,
                                      ServiceTokenGetter tokenGetter) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
        this.tokenGetter = requireNonNull(tokenGetter, "tokenGetter");
    }

    public List<Beholdning> getPensjonsbeholdning(String fnr) {
        try {
            var response = webClient
                    .post()
                    .uri(beholdningUri())
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .bodyValue(new BeholdningListeRequest(fnr))
                    .retrieve()
                    .bodyToMono(BeholdningListeResponse.class)
                    .block();

            return response == null ? null : fromDto(response.getBeholdninger());
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
        return new PingInfo("REST", "POPP pensjonsbeholdning", beholdningUri());
    }

    private String beholdningUri() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .path(SUB_PATH)
                .toUriString();
    }

    private String pingUri() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .path(SUB_PATH + "/ping")
                .toUriString();
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getServiceUserToken().getAccessToken();
    }
}
