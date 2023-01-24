package no.nav.pensjon.selvbetjeningopptjening.tjenestepensjon;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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

    public TjenestepensjonClient(WebClient webClient, @Value("${tp.url}") String tpUrl) {
        this.webClient = requireNonNull(webClient);
        this.tpUrl = requireNonNull(tpUrl);
    }

    public List<Tjenestepensjonsforhold> getAllTjenestepensjonsforhold(String pid) {
        return webClient
                .get()
                .uri(buildUri(pid))
                .header(HttpHeaders.AUTHORIZATION, getAuthHeaderValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Tjenestepensjonsforhold>>() {
                })
                .block();
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
