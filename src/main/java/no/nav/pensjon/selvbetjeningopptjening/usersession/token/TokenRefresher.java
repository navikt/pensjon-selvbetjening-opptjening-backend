package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RefreshToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public abstract class TokenRefresher {

    private final TokenGetter tokenGetter;

    protected TokenRefresher(TokenGetter tokenGetter) {
        this.tokenGetter = requireNonNull(tokenGetter);
    }

    public TokenData refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new MissingTokenException("No refresh token in request (no cookies)");
        }

        return Arrays
                .stream(cookies)
                .filter(this::hasRefreshToken)
                .findFirst()
                .map(this::getRefreshedToken)
                .orElseThrow(() -> new MissingTokenException("No refresh token in request"));
    }

    private boolean hasRefreshToken(Cookie cookie) {
        return CookieType.REFRESH_TOKEN.getName().equals(cookie.getName());
    }

    private TokenData getRefreshedToken(Cookie cookie) {
        return tokenGetter.getTokenData(TokenAccessParam.refreshToken(new RefreshToken(cookie.getValue())), "-");
    }
}
