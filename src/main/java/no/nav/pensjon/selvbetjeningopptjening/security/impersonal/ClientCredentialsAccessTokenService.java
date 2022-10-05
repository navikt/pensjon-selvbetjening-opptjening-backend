package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.aad.AzureAdUtil.getDefaultScope;

/**
 * Gets access tokens in public cloud (Google Cloud Platform - GCP) for impersonal use (access as application).
 */
@Service
public class ClientCredentialsAccessTokenService implements AccessTokenGetter {

    private final static String USED_BY = "application";
    private final CacheAwareTokenClient tokenGetter;

    public ClientCredentialsAccessTokenService(CacheAwareTokenClient tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    /**
     * The 'audience' argument will be used as scope parameter in the token request.
     */
    @Override
    public RawJwt getAccessToken(String audience) {
        String scope = getDefaultScope(audience);
        TokenAccessParam accessParam = TokenAccessParam.clientCredentials(scope);
        String tokenValue = tokenGetter.getTokenData(accessParam, scope, USED_BY).getAccessToken();
        return new RawJwt(tokenValue);
    }

    @Override
    public void clearAccessToken(String audience) {
        tokenGetter.clearTokenData(audience, USED_BY);
    }
}
