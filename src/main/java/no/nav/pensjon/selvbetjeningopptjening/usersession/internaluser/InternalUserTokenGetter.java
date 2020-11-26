package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.OidcTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.requireNonNull;

@Component
@Qualifier("internal-user")
public class InternalUserTokenGetter extends OidcTokenGetter {

    private static final String OAUTH_2_SCOPE = "user.read";
    private final String clientId;
    private final String clientSecret;
    private final String callbackUri;

    public InternalUserTokenGetter(@Qualifier("external-call") WebClient webClient,
                                   @Qualifier("internal-user") OidcConfigGetter oidcConfigGetter,
                                   @Value("${internal-user.openid.client-id}") String clientId,
                                   @Value("${internal-user.openid.client-secret}") String clientSecret,
                                   @Value("${internal-user.openid.redirect-uri}") String callbackUri) {
        super(webClient, oidcConfigGetter);
        this.clientId = requireNonNull(clientId);
        this.clientSecret = requireNonNull(clientSecret);
        this.callbackUri = requireNonNull(callbackUri);
    }

    @Override
    protected MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam) {
        return new Oauth2ParamBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope(OAUTH_2_SCOPE)
                .tokenAccessParam(accessParam)
                .callbackUri(callbackUri)
                .buildClientIdTokenRequestMap();
    }
}
