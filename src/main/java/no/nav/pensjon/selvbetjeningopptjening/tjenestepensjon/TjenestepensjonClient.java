package no.nav.pensjon.selvbetjeningopptjening.tjenestepensjon;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
public class TjenestepensjonClient {

    private static final String API_PATH = "/api/tjenestepensjon/forhold";
    private static final String AUTH_TYPE = "Bearer";
    private final WebClient webClient;
    private final String tpUrl;
    private static final String SERVICE = "TP";

    public TjenestepensjonClient(WebClient webClient, @Value("${tp.url}") String tpUrl) {
        this.webClient = requireNonNull(webClient);
        this.tpUrl = requireNonNull(tpUrl);
    }

    public List<Tjenestepensjonsforhold> getAllTjenestepensjonsforhold(String pid) {
        try {
            return webClient
                    .get()
                    .uri(buildUri(pid))
                    .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Tjenestepensjonsforhold>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new FailedCallingExternalServiceException(SERVICE, "finnFullmakter", "Failed to call service: " + e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) { // e.g. when connection broken
            throw new FailedCallingExternalServiceException(SERVICE, "finnFullmakter", "Failed to call service", e);
        }
    }

    private URI buildUri(String pid) {
        var factory = new DefaultUriBuilderFactory(tpUrl);
        return factory
                .uriString(API_PATH + "/" + pid)
                .build();
    }

    private String getAuthHeaderValue() {
        return AUTH_TYPE + " " + RequestContext.getEgressAccessToken(AppIds.TJENESTEPENSJON).getValue();
    }
}
