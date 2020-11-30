package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class CookieSetterTest {

    private CookieSetter cookieSetter;

    @Mock
    HttpServletResponse response;

    @Captor
    ArgumentCaptor<Cookie> cookieCaptor;

    @BeforeEach
    void setUp() {
        cookieSetter = new CookieSetter(false);
    }

    @Test
    void setCookie_for_externalUserIdToken_results_in_secure_and_httpOnly_setCookie_header() {
        cookieSetter.setCookie(response, CookieType.EXTERNAL_USER_ID_TOKEN, "foo");

        verify(response, times(1)).addCookie(cookieCaptor.capture());
        Cookie actual = cookieCaptor.getValue();
        assertEquals("xu-idtoken", actual.getName());
        assertEquals("foo", actual.getValue());
        assertEquals("/", actual.getPath());
        assertTrue(actual.isHttpOnly());
        assertTrue(actual.getSecure());
    }

    @Test
    void setCookie_for_internalUserIdToken_results_in_secure_and_httpOnly_setCookie_header() {
        cookieSetter.setCookie(response, CookieType.INTERNAL_USER_ID_TOKEN, "foo");

        verify(response, times(1)).addCookie(cookieCaptor.capture());
        Cookie actual = cookieCaptor.getValue();
        assertEquals("iu-idtoken", actual.getName());
        assertEquals("foo", actual.getValue());
        assertEquals("/", actual.getPath());
        assertTrue(actual.isHttpOnly());
        assertTrue(actual.getSecure());
    }

    @Test
    void setCookie_for_refreshToken_results_in_secure_and_httpOnly_setCookie_header() {
        cookieSetter.setCookie(response, CookieType.REFRESH_TOKEN, "foo");

        verify(response, times(1)).addCookie(cookieCaptor.capture());
        Cookie actual = cookieCaptor.getValue();
        assertEquals("refresh-token", actual.getName());
        assertEquals("foo", actual.getValue());
        assertEquals("/", actual.getPath());
        assertTrue(actual.isHttpOnly());
        assertTrue(actual.getSecure());
    }
}
