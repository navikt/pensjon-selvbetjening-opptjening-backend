package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser.obo;

import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Gets access tokens for internal users (NAV employees).
 * Adapter for TokenGetter, which obtains tokens for personal users.
 */
@Component
@Qualifier("internal-user")
public class InternalUserAccessTokenGetter implements AccessTokenGetter {

    private final CacheAwareTokenClient tokenGetter;

    public InternalUserAccessTokenGetter(@Qualifier("aad") CacheAwareTokenClient tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    @Override
    public RawJwt getAccessToken(String assertion, String audience, String pid) {
        TokenAccessParam accessParam = TokenAccessParam.jwtBearer(assertion);
        String tokenValue = tokenGetter.getTokenData(accessParam, audience, pid).getAccessToken();
        return new RawJwt(tokenValue);
    }

    @Override
    public void clearAccessToken(String audience, String pid) {
        tokenGetter.clearTokenData(audience, pid);
    }
}
