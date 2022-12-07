package no.nav.pensjon.selvbetjeningopptjening.security.token.client;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2Scopes;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Obtains access token and ID token that can be used by clients for accessing this app (after a successful login).
 * The access token is also used for obtaining on-behalf-of access tokens so that this app can access other apps
 * (e.g. PDL) on behalf of the logged-in user.
 * The logged-in user must be a NAV employee (internal user).
 * The token is sent to the client and hence not stored server-side.
 */
@Component
@Qualifier("internal-user")
public class ClientIdBasedTokenClient extends Oauth2TokenClient {

    private static final String APP_NAMESPACE = "pensjonselvbetjening";
    private final String appName;
    private final String clusterName;
    private final String clientId;
    private final String clientSecret;
    private final String callbackUri;

    public ClientIdBasedTokenClient(WebClient webClient,
                                    @Qualifier("internal-user") Oauth2ConfigGetter oauth2ConfigGetter,
                                    ExpirationChecker expirationChecker,
                                    @Value("${nais.cluster.name}") String clusterName,
                                    @Value("${nais.app.name}") String appName,
                                    @Value("${internal-user.oauth2.client-id}") String clientId,
                                    @Value("${internal-user.oauth2.client-secret}") String clientSecret,
                                    @Value("${internal-user.oauth2.redirect-uri}") String callbackUri) {
        super(webClient, expirationChecker, oauth2ConfigGetter);
        this.clusterName = requireNonNull(clusterName);
        this.appName = requireNonNull(appName);
        this.clientId = requireNonNull(clientId);
        this.clientSecret = requireNonNull(clientSecret);
        this.callbackUri = requireNonNull(callbackUri);
    }

    @Override
    protected MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam, String unused) {
        return new Oauth2ParamBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope(scope())
                .tokenAccessParam(accessParam)
                .callbackUri(callbackUri)
                .buildClientIdTokenRequestMap();
    }

    private String scope() {
        return format("api://%s.%s.%s/%s", clusterName, APP_NAMESPACE, appName, Oauth2Scopes.AAD_DEFAULT);
    }
}
