package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.aad.AzureAdUtil.getDefaultScope;

/**
 * Gets access tokens for impersonal use (access as application).
 */
@Service
public class ClientCredentialsAccessTokenService implements AccessTokenGetter {

    private final static String USED_BY = "application";
    private final CacheAwareTokenClient tokenGetter;

    public ClientCredentialsAccessTokenService(@Qualifier("client-credentials") CacheAwareTokenClient tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    /**
     * The 'audience' argument will be used as scope parameter in the token request.
     * The 'ingressToken' and 'pid' arguments are irrelevant here, as there is no user request involved.
     */
    @Override
    public RawJwt getAccessToken(String ingressToken, String audience, String pid) {
        String scope = getDefaultScope(audience);
        TokenAccessParam accessParam = TokenAccessParam.clientCredentials(scope);
        String tokenValue = tokenGetter.getTokenData(accessParam, scope, USED_BY).getAccessToken();
        return new RawJwt(tokenValue);
    }

    /**
     * The 'pid' argument is irrelevant here, as the "user" is the application itself.
     */
    @Override
    public void clearAccessToken(String audience, String pid) {
        tokenGetter.clearTokenData(audience, USED_BY);
    }
}
