package no.nav.pensjon.selvbetjeningopptjening.consumer.skjerming;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.group.SkjermingApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Component
public class SkjermingConsumer implements SkjermingApi {

    private static final String PATH = "/skjermet?personident=";
    private static final boolean DEFAULT_IS_SKJERMET = true;
    private static final Logger log = LoggerFactory.getLogger(SkjermingConsumer.class);
    private final WebClient webClient;
    private final String url;

    public SkjermingConsumer(@Value("${skjerming.url}") String baseUrl) {
        this.webClient = WebClient.create();
        this.url = requireNonNull(baseUrl, "baseUrl") + PATH;
    }

    @Override
    public boolean isSkjermet(Pid pid) {
        try {
            Boolean isSkjermet = webClient
                    .get()
                    .uri(url + pid.getPid())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return isSkjermet == null ? DEFAULT_IS_SKJERMET : isSkjermet;
        } catch (WebClientResponseException e) {
            log.error(format("Call to Skjerming API failed: %s. Response body: %s.",
                    e.getMessage(), e.getResponseBodyAsString()));
            return DEFAULT_IS_SKJERMET;
        } catch (RuntimeException e) { // e.g. when connection broken
            log.error(format("Call to Skjerming API failed: %s.", e.getMessage()));
            return DEFAULT_IS_SKJERMET;
        }
    }
}
