package no.nav.pensjon.selvbetjeningopptjening.consumer.skjerming;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.group.SkjermingApi;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;
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

    public SkjermingConsumer(WebClient webClient,
                             @Value("${skjerming.url}") String baseUrl,
                             TokenGetterFacade tokenGetter) {
        this.webClient = requireNonNull(webClient, "webClient");
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
        this.tokenGetter = requireNonNull(tokenGetter, "tokenGetter");
    }

    @Override
    public boolean isSkjermet(Pid pid) {
        if (log.isDebugEnabled()) {
            log.debug("Calling {} for PID {}", SERVICE, maskFnr(pid));
        }

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
        } catch (WebClientResponseException e) {
            log.error("Call to {} failed. Response body: {}.", SERVICE, e.getResponseBodyAsString(), e);
            return DEFAULT_IS_SKJERMET;
        } catch (RuntimeException e) { // e.g. when connection broken
            log.error("Call to {} failed", SERVICE, e);
            return DEFAULT_IS_SKJERMET;
        }
    }

    private String getAuthHeaderValue() {
        return AUTH_TYPE + " " + tokenGetter.getToken(AppIds.SKJERMEDE_PERSONER_PIP.appName);
    }
}
