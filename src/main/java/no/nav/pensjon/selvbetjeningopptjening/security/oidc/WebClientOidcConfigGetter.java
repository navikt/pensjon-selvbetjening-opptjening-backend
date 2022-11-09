package no.nav.pensjon.selvbetjeningopptjening.security.oidc;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.OidcConfigDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.requireNonNull;

@Component
@Qualifier("internal-user")
public class WebClientOidcConfigGetter implements OidcConfigGetter {

    private final WebClient webClient;
    private final String configUrl;
    private OidcConfigDto config;

    public WebClientOidcConfigGetter(WebClient webClient,
                                     @Value("${internal-user.openid.well-known-url}") String configUrl) {
        this.webClient = requireNonNull(webClient);
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
