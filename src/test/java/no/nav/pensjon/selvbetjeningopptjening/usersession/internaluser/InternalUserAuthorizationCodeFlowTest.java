package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.CookieBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.RequestBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSpec;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LegacyLogin;
import no.nav.pensjon.selvbetjeningopptjening.usersession.Logout;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenRefresher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalUserAuthorizationCodeFlow.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class InternalUserAuthorizationCodeFlowTest {

    private static final Pid PID = new Pid("04925398980");
    private static final String ROOT_URL = "/oauth2/internal";
    private static final String LOGIN_URL = ROOT_URL + "/login";
    private static final String CALLBACK_URL = ROOT_URL + "/callback";
    private static final String REFRESH_TOKEN_URL = ROOT_URL + "/refresh-token";

    @Autowired
    private MockMvc mvc;
    @MockBean
    @Qualifier("internal-user")
    private Oauth2ConfigGetter oauth2ConfigGetter;
    @MockBean
    @Qualifier("internal-user")
    private TokenGetter tokenGetter;
    @MockBean
    @Qualifier("internal-user")
    private TokenRefresher tokenRefresher;
    @MockBean
    @Qualifier("internal-user")
    private JwsValidator jwsValidator;
    @MockBean
    private CookieSetter cookieSetter;
    @MockBean
    private Crypto crypto;
    @MockBean
    private IngressTokenFinder ingressTokenFinder;
    @MockBean
    private EgressAccessTokenFacade egressAccessTokenFacade;
    @MockBean
    private TokenAudiencesVsApps tokenAudiencesVsApps;
    @MockBean
    @Qualifier("internal-user")
    private LegacyLogin legacyLogin;
    @MockBean
    private Logout logout;
    @MockBean
    private CookieBasedBrukerbytte brukerbytte;
    @MockBean
    private GroupChecker groupChecker;
    @MockBean
    private Auditor auditor;
    @MockBean
    private RequestBasedBrukerbytte requestBased;
    @Mock
    private Claims claims;
    @Captor
    private ArgumentCaptor<List<CookieSpec>> cookieCaptor;

    @BeforeEach
    void initialize() {
        when(ingressTokenFinder.getIngressTokenInfo(any(), anyBoolean())).thenReturn(TokenInfo.valid("jwt1", UserType.INTERNAL, claims, PID.getPid()));
    }

    @Test
    void login_with_redirectUri_has_givenUri_as_stateQueryParam() throws Exception {
        when(oauth2ConfigGetter.getAuthorizationEndpoint()).thenReturn("http://auth.org");
        when(crypto.encrypt(anyString())).thenReturn("cryptic");

        mvc.perform(get(LOGIN_URL)
                .param("redirect", "/foo/bar?fnr=" + PID.getPid()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://auth.org" +
                        "?scope=openid+profile+offline_access+https%3A%2F%2Fgraph.microsoft.com%2Fuser.read" +
                        "&response_type=code" +
                        "&redirect_uri=https%3A%2F%2Fpensjon-selvbetjening-opptjening-backend.dev.intern.nav.no%2Foauth2%2Finternal%2Fcallback" +
                        "&state=cryptic" +
                        "&client_id=5d863b8b-5fd5-47d4-8c9b-7a78a534fb1b" +
                        "&response_mode=form_post"));
    }

    @Test
    void login_without_redirectUri_has_defaultUri_as_stateQueryParam() throws Exception {
        when(oauth2ConfigGetter.getAuthorizationEndpoint()).thenReturn("http://auth.org");
        when(crypto.encrypt(anyString())).thenReturn("cryptic");

        mvc.perform(get(LOGIN_URL))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://auth.org" +
                        "?scope=openid+profile+offline_access+https%3A%2F%2Fgraph.microsoft.com%2Fuser.read" +
                        "&response_type=code" +
                        "&redirect_uri=https%3A%2F%2Fpensjon-selvbetjening-opptjening-backend.dev.intern.nav.no%2Foauth2%2Finternal%2Fcallback" +
                        "&state=cryptic" +
                        "&client_id=5d863b8b-5fd5-47d4-8c9b-7a78a534fb1b" +
                        "&response_mode=form_post"));
    }

    @Test
    void callback_with_state_redirects_to_given_uri() throws Exception {
        var tokenData = new TokenData("access-token", "ID-token", "refresh-token", LocalDateTime.MIN, 1L);
        when(tokenGetter.getTokenData(any(TokenAccessParam.class), anyString())).thenReturn(tokenData);
        when(crypto.decrypt(anyString())).thenReturn(currentTimeMillis() + ":/api/foo?fnr=" + PID.getPid());

        mvc.perform(post(CALLBACK_URL)
                .param("code", "abc")
                .param("state", "cryptic"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/foo?fnr=" + PID.getPid()));

        verifyCookie();
    }

    @Test
    void refreshToken_redirects_ok_when_refresher_returns_token() throws Exception {
        var tokenData = new TokenData("access-token", "new-ID-token", "new-refresh-token", LocalDateTime.MIN, 1L);
        when(tokenRefresher.refreshToken(any(HttpServletRequest.class))).thenReturn(tokenData);

        mvc.perform(getWithRefreshTokenCookie())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/opptjening"));

        verifyCookie();
    }

    @Test
    void refreshToken_responds_with_unauthorized_when_refresher_throws_JwtException() throws Exception {
        when(tokenRefresher.refreshToken(any(HttpServletRequest.class))).thenThrow(new JwtException("oops"));

        mvc.perform(getWithRefreshTokenCookie())
                .andExpect(status().isUnauthorized());
    }

    private void verifyCookie() {
        verify(cookieSetter, times(1)).setCookies(any(), cookieCaptor.capture());
        CookieSpec actual = cookieCaptor.getValue().get(0);
        assertEquals(CookieType.INTERNAL_USER_ACCESS_TOKEN, actual.getCookieType());
        assertEquals("access-token", actual.getCookieValue());
    }

    private static MockHttpServletRequestBuilder getWithRefreshTokenCookie() {
        return get(REFRESH_TOKEN_URL)
                .cookie(new Cookie("refresh-token", "current-refresh-token"));
    }
}
