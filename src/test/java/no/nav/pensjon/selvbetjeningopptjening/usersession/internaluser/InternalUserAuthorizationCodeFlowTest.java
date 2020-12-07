package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenRefresher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.System.currentTimeMillis;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalUserAuthorizationCodeFlow.class)
class InternalUserAuthorizationCodeFlowTest {

    private static final String ROOT_URL = "/oauth2/internal";
    private static final String LOGIN_URL = ROOT_URL + "/login";
    private static final String CALLBACK_URL = ROOT_URL + "/callback";
    private static final String REFRESH_TOKEN_URL = ROOT_URL + "/refresh-token";

    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("internal-user")
    OidcConfigGetter oidcConfigGetter;

    @MockBean
    @Qualifier("internal-user")
    TokenGetter tokenGetter;

    @MockBean
    @Qualifier("internal-user")
    TokenRefresher tokenRefresher;

    @MockBean
    @Qualifier("internal-user")
    JwsValidator jwsValidator;

    @MockBean
    CookieSetter cookieSetter;

    @MockBean
    Crypto crypto;

    @Test
    void login_with_redirectUri_has_givenUri_as_stateQueryParam() throws Exception {
        when(oidcConfigGetter.getAuthorizationEndpoint()).thenReturn("http://auth.org");
        when(crypto.encrypt(anyString())).thenReturn("cryptic");

        mvc.perform(get(LOGIN_URL)
                .param("redirect", "/foo/bar"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://auth.org" +
                        "?scope=openid+profile+offline_access+https%3A%2F%2Fgraph.microsoft.com%2Fuser.read" +
                        "&response_type=code" +
                        "&redirect_uri=https%3A%2F%2Fpensjon-selvbetjening-opptjening-backend.dev.intern.nav.no%2Foauth2%2Finternal%2Fcallback" +
                        "&state=cryptic" +
                        "&client_id=aad-client-id" +
                        "&response_mode=form_post"));
    }

    @Test
    void login_without_redirectUri_has_defaultUri_as_stateQueryParam() throws Exception {
        when(oidcConfigGetter.getAuthorizationEndpoint()).thenReturn("http://auth.org");
        when(crypto.encrypt(anyString())).thenReturn("cryptic");

        mvc.perform(get(LOGIN_URL))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://auth.org" +
                        "?scope=openid+profile+offline_access+https%3A%2F%2Fgraph.microsoft.com%2Fuser.read" +
                        "&response_type=code" +
                        "&redirect_uri=https%3A%2F%2Fpensjon-selvbetjening-opptjening-backend.dev.intern.nav.no%2Foauth2%2Finternal%2Fcallback" +
                        "&state=cryptic" +
                        "&client_id=aad-client-id" +
                        "&response_mode=form_post"));
    }

    @Test
    void callback_with_state_redirects_to_given_uri() throws Exception {
        var tokenData = new TokenData("access-token", "ID-token", "refresh-token");
        when(tokenGetter.getTokenData(any(TokenAccessParam.class))).thenReturn(tokenData);
        when(crypto.decrypt(anyString())).thenReturn(currentTimeMillis() + ":/api/foo");

        mvc.perform(post(CALLBACK_URL)
                .param("code", "abc")
                .param("state", "cryptic"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/foo"));

        verifyCookie(CookieType.INTERNAL_USER_ID_TOKEN, "ID-token");
        verifyCookie(CookieType.REFRESH_TOKEN, "refresh-token");
    }

    @Test
    void refreshToken_redirects_ok_when_refresher_returns_token() throws Exception {
        var tokenData = new TokenData("access-token", "new-ID-token", "new-refresh-token");
        when(tokenRefresher.refreshToken(any(HttpServletRequest.class))).thenReturn(tokenData);

        mvc.perform(getWithRefreshTokenCookie())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/opptjening"));

        verifyCookie(CookieType.INTERNAL_USER_ID_TOKEN, "new-ID-token");
        verifyCookie(CookieType.REFRESH_TOKEN, "new-refresh-token");
    }

    @Test
    void refreshToken_responds_with_unauthorized_when_refresher_throws_JwtException() throws Exception {
        when(tokenRefresher.refreshToken(any(HttpServletRequest.class))).thenThrow(new JwtException("oops"));

        mvc.perform(getWithRefreshTokenCookie())
                .andExpect(status().isUnauthorized());
    }

    private void verifyCookie(CookieType type, String expectedValue) {
        verify(cookieSetter, times(1))
                .setCookie(any(HttpServletResponse.class), eq(type), eq(expectedValue));
    }

    private static MockHttpServletRequestBuilder getWithRefreshTokenCookie() {
        return get(REFRESH_TOKEN_URL)
                .cookie(new Cookie("refresh-token", "current-refresh-token"));
    }
}
