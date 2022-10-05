package no.nav.pensjon.selvbetjeningopptjening.consumer.skjerming;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.group.SkjermingApi;
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

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;

@Component
public class SkjermingConsumer implements SkjermingApi {

    private static final String SERVICE = "Skjerming";
    private static final String PATH = "/skjermet?personident=";
    private static final String AUTH_TYPE = "Bearer";
    private static final boolean DEFAULT_IS_SKJERMET = true;
    private static final Logger log = LoggerFactory.getLogger(SkjermingConsumer.class);
    private final WebClient webClient;
    private final String url;
    private final TokenGetterFacade tokenGetter;

    public SkjermingConsumer(@Value("${skjerming.url}") String baseUrl,
                             TokenGetterFacade tokenGetter) {
        this.tokenGetter = tokenGetter;
        this.webClient = WebClient.create();
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
    }

    @Override
    public boolean isSkjermet(Pid pid) {
        try {
            Boolean isSkjermet = webClient
                    .get()
                    .uri(url + pid.getPid())
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .header(NAV_CALL_ID, MDC.get(NAV_CALL_ID))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return isSkjermet == null ? DEFAULT_IS_SKJERMET : isSkjermet;
        } catch (StsException e) {
            log.error("STS error when calling {}", SERVICE, e);
            return DEFAULT_IS_SKJERMET;
        } catch (WebClientResponseException e) {
            log.error("Call to {} failed. Response body: {}.",
                    SERVICE, e.getResponseBodyAsString(), e);
            return DEFAULT_IS_SKJERMET;
        } catch (RuntimeException e) { // e.g. when connection broken
            log.error("Call to {} failed", SERVICE, e);
            return DEFAULT_IS_SKJERMET;
        }
    }

    private String getAuthHeaderValue() throws StsException {
        return AUTH_TYPE + " " + tokenGetter.getToken(AppIds.SKJERMEDE_PERSONER_PIP.appName);
    }
}
