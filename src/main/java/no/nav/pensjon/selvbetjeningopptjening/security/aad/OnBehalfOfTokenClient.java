package no.nav.pensjon.selvbetjeningopptjening.security.aad;

import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2Scopes;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Obtains access tokens for accessing other apps on behalf of the logged-in user.
 * This is relevant for users that are NAV employees (internal users).
 */
@Component
@Qualifier("aad")
public class OnBehalfOfTokenClient extends CacheAwareTokenClient {

    private final String clientId;
    private final String clientSecret;

    public OnBehalfOfTokenClient(@Qualifier("internal-user") Oauth2ConfigGetter oauth2ConfigGetter,
                                 ExpirationChecker expirationChecker,
                                 //TokenRepository tokenRepository,
                                 @Value("${internal-user.oauth2.client-id}") String clientId,
                                 @Value("${internal-user.oauth2.client-secret}") String clientSecret) {
        super(webClient(), oauth2ConfigGetter, expirationChecker);
        this.clientId = requireNonNull(clientId);
        this.clientSecret = requireNonNull(clientSecret);
    }

    @Override
    protected MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam, String audience) {
        return new Oauth2ParamBuilder()
                .scope(appScope(audience))
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tokenAccessParam(accessParam)
                .buildOnBehalfOfTokenRequestMap();
    }

    private static WebClient webClient() {
        var httpClient = HttpClient.create()
                .wiretap(true);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private static String appScope(String appId) {
        String scopeAppId = appId.replace(":", ".");
        return format("api://%s/%s", scopeAppId, Oauth2Scopes.AAD_DEFAULT);
    }
}
