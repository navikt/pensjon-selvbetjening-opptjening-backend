package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.requireNonNull;

public class Oauth2ConfigClient implements Oauth2ConfigGetter {

    private final WebClient webClient;
    private final String configUrl;
    private Oauth2ConfigResponse cachedConfig;

    public Oauth2ConfigClient(WebClient webClient, String configUrl) {
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
    public String getEndSessionEndpoint() {
        return getCachedConfig().getEndSessionEndpoint();
    }

    @Override
    public String getJsonWebKeySetUri() {
        return getCachedConfig().getJwksUri();
    }

    @Override
    public void refresh() {
        cachedConfig = null;
    }

    private Oauth2ConfigResponse getFreshConfig() {
        return webClient
                .get()
                .uri(configUrl)
                .retrieve()
                .bodyToMono(Oauth2ConfigResponse.class)
                .block();
    }

    private Oauth2ConfigResponse getCachedConfig() {
        return cachedConfig == null
                ? (cachedConfig = getFreshConfig())
                : cachedConfig;
    }
}
