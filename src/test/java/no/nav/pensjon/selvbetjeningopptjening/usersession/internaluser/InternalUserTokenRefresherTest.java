package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.usersession.token.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InternalUserTokenRefresherTest {

    private TokenRefresher tokenRefresher;

    @Mock
    private HttpServletRequest request;
    @Mock
    private TokenGetter tokenGetter;

    @BeforeEach
    void setUp() {
        tokenRefresher = new InternalUserTokenRefresher(tokenGetter);
    }

    @Test
    void refreshToken_returns_new_tokens_when_refreshTokenCookie_exists() {
        Cookie[] cookies = {new Cookie("refresh-token", "current refresh token")};
        when(request.getCookies()).thenReturn(cookies);
        var expected = new TokenData("access-token", "new ID token", "new refresh token", LocalDateTime.MIN, 1L);
        when(tokenGetter.getTokenData(any(TokenAccessParam.class), anyString())).thenReturn(expected);

        TokenData actual = tokenRefresher.refreshToken(request);

        assertEquals(expected.getIdToken(), actual.getIdToken());
        assertEquals(expected.getRefreshToken(), actual.getRefreshToken());
    }

    @Test
    void refreshToken_throws_MissingTokenException_when_cookie_is_not_refreshTokenCookie() {
        Cookie[] cookies = {new Cookie("irrelevant", "irrelevant")};
        when(request.getCookies()).thenReturn(cookies);

        MissingTokenException e = assertThrows(MissingTokenException.class, () -> tokenRefresher.refreshToken(request));

        assertEquals("No refresh token in request", e.getMessage());
    }

    @Test
    void refreshToken_throws_MissingTokenException_when_no_cookies() {
        Cookie[] noCookies = {};
        when(request.getCookies()).thenReturn(noCookies);

        MissingTokenException e = assertThrows(MissingTokenException.class, () -> tokenRefresher.refreshToken(request));

        assertEquals("No refresh token in request", e.getMessage());
    }

    @Test
    void refreshToken_throws_MissingTokenException_when_null_cookies() {
        when(request.getCookies()).thenReturn(null);
        MissingTokenException e = assertThrows(MissingTokenException.class, () -> tokenRefresher.refreshToken(request));
        assertEquals("No refresh token in request (no cookies)", e.getMessage());
    }
}
