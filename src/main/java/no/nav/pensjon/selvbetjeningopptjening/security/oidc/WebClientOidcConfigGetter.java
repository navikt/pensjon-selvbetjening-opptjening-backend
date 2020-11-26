package no.nav.pensjon.selvbetjeningopptjening.security.oidc;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.OidcConfigDto;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.requireNonNull;

public class WebClientOidcConfigGetter implements OidcConfigGetter {

    private final WebClient webClient;
    private final String configUrl;
    private OidcConfigDto config;

    public WebClientOidcConfigGetter(String configUrl) {
        this.webClient = WebClient.create();
        this.configUrl = requireNonNull(configUrl);
    }

    @Override
    public String getIssuer() {
        return getCachedConfig().getIssuer();
    }

    @Override
    public String getAuthorizationEndpoint() {
        return getCachedConfig().getAuthorizationEndpoint();
    }

    @Override
    public String getTokenEndpoint() {
        return getCachedConfig().getTokenEndpoint();
    }

    @Override
    public String getJsonWebKeySetUri() {
        return getCachedConfig().getJwksUri();
    }

    @Override
    public void refresh() {
        config = null;
    }

    private OidcConfigDto getFreshConfig() {
        return webClient
                .get()
                .uri(configUrl)
                .retrieve()
                .bodyToMono(OidcConfigDto.class)
                .block();
    }

    private OidcConfigDto getCachedConfig() {
        return config == null
                ? (config = getFreshConfig())
                : config;
    }
}
