package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.requireNonNull;

/**
 * Client for requesting access token based on client credentials grant.
 */
@Component
public class ClientCredentialsTokenRequestClient extends CacheAwareTokenClient {

    private final ClientCredentials credentials;

    public ClientCredentialsTokenRequestClient(@Qualifier("external-call") WebClient webClient,
                                               Oauth2ConfigGetter oauth2ConfigGetter,
                                               ExpirationChecker expirationChecker,
                                               ClientCredentials credentials) {
        super(webClient, oauth2ConfigGetter, expirationChecker);
        this.credentials = requireNonNull(credentials, "credentials");
    }

    @Override
    protected MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam, String unused) {
        return new Oauth2ParamBuilder()
                .tokenAccessParam(accessParam)
                .clientId(credentials.getClientId())
                .clientSecret(credentials.getClientSecret())
                .buildClientCredentialsTokenRequestMap();
    }
}
