package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class CookieSetterTest {

    private CookieSetter cookieSetter;

    @Mock
    HttpServletResponse response;

    @BeforeEach
    void setUp() {
        cookieSetter = new CookieSetter("domain", false);
    }

    @Test
    void setCookies_for_externalUserAccessToken_results_in_secure_and_httpOnly_setCookie_header() {
        cookieSetter.setCookies(response, List.of(new CookieSpec(CookieType.EXTERNAL_USER_ACCESS_TOKEN, "foo")));

        verify(response, times(1)).setHeader("Set-Cookie",
                "xu-acctoken=foo; Domain=domain; Path=/; SameSite=Lax; Max-Age=7200; Secure; HttpOnly");
    }

    @Test
    void unsetCookie_sets_blankValue_and_zeroMaxAge() {
        cookieSetter.unsetCookie(response, CookieType.EXTERNAL_USER_ACCESS_TOKEN);

        verify(response, times(1)).setHeader("Set-Cookie",
                "xu-acctoken=; Domain=domain; Path=/; SameSite=Lax; Max-Age=0; Secure; HttpOnly");
    }
}
