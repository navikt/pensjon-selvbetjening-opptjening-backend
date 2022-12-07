package no.nav.pensjon.selvbetjeningopptjening.usersession.externaluser.tokenx;

import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;

import static java.util.Objects.requireNonNull;

/**
 * Gets access tokens for external users.
 * Adapter for TokenGetter, which obtains tokens for personal users.
 */
public class ExternalUserAccessTokenGetter implements AccessTokenGetter {

    private final CacheAwareTokenClient tokenGetter;

    public ExternalUserAccessTokenGetter(CacheAwareTokenClient tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    @Override
    public RawJwt getAccessToken(String ingressToken, String audience, String pid) {
        TokenAccessParam accessParam = TokenAccessParam.tokenExchange(ingressToken);
        String tokenValue = tokenGetter.getTokenData(accessParam, audience, pid).getAccessToken();
        return new RawJwt(tokenValue);
    }

    @Override
    public void clearAccessToken(String audience, String pid) {
        tokenGetter.clearTokenData(audience, pid);
    }
}
