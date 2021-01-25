package no.nav.pensjon.selvbetjeningopptjening.consumer.skjerming;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.group.SkjermingApi;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Component
public class SkjermingConsumer implements SkjermingApi {

    private static final String SKJERMET_ENDPOINT = "skjermet?personident=";
    private final WebClient webClient;
    private final String baseUrl;
    private final Log log = LogFactory.getLog(getClass());

    public SkjermingConsumer(@Value("${skjerming.endpoint.url}") String baseUrl) {
        this.webClient = WebClient.create();
        this.baseUrl = requireNonNull(baseUrl);
    }

    @Override
    public boolean isEgenAnsatt(Pid pid) {
        try {
            Boolean isSkjermet = webClient
                    .post()
                    .uri(baseUrl + SKJERMET_ENDPOINT + pid.getPid())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return isSkjermet == null ? false : isSkjermet;
        } catch (WebClientResponseException e) {
            log.error(format("Call to Skjerming API failed: %s. Response body: %s.",
                    e.getMessage(), e.getResponseBodyAsString()));
            return false;
        }
    }
}
