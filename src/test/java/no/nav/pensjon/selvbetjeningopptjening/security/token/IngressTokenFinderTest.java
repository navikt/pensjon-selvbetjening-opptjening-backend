package no.nav.pensjon.selvbetjeningopptjening.security.token;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class IngressTokenFinderTest {

    private static final String USER_ID = "user1";
    private static final String TOKEN = "j.w.t";
    private static final String INVALID_TOKEN = "invalid";
    private static final String COOKIE_NAME_1 = "iu-acctoken";
    private static final String COOKIE_NAME_2 = "xu-acctoken";
    private IngressTokenFinder tokenFinder;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private JwsValidator jwsValidator;
    @Mock
    private Claims claims;
    @Mock
    private TokenRefresherFacade tokenRefresher;

    @BeforeEach
    void initialize() {
        tokenFinder = new IngressTokenFinder(jwsValidator, tokenRefresher);
    }

    @Test
    void when_validBearerToken_then_getIngressTokenInfo_returnsTokenInfoWithStatusValid() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + TOKEN);
        when(jwsValidator.validate(TOKEN)).thenReturn(TokenInfo.valid(TOKEN, UserType.EXTERNAL, claims, USER_ID));

        TokenInfo tokenInfo = tokenFinder.getIngressTokenInfo(request, false);

        assertTrue(tokenInfo.isValid());
    }

    @Test
    void when_validTokenInCookie_then_getIngressTokenInfo_returnsTokenInfoWithStatusValid() {
        when(request.getSession()).thenReturn(session);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NAME_1, TOKEN)});
        when(jwsValidator.validate(TOKEN)).thenReturn(TokenInfo.valid(TOKEN, UserType.EXTERNAL, claims, USER_ID));

        TokenInfo tokenInfo = tokenFinder.getIngressTokenInfo(request, false);

        assertTrue(tokenInfo.isValid());
    }

    @Test
    void when_internalUser_and_validTokenInCookie_then_getIngressTokenInfo_returnsTokenInfoWithStatusValid() {
        arrangePidInQueryString();
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NAME_1, TOKEN)});
        when(jwsValidator.validate(TOKEN)).thenReturn(TokenInfo.valid(TOKEN, UserType.INTERNAL, claims, USER_ID));

        TokenInfo tokenInfo = tokenFinder.getIngressTokenInfo(request, false);

        assertTrue(tokenInfo.isValid());
    }

    @Test
    void when_internalUser_and_queryStringWithoutPid_and_validTokenInCookie_then_getIngressTokenInfo_returnsTokenInfoWithStatusValid() {
        arrangeQueryStringWithoutPid();
        when(request.getSession()).thenReturn(session);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NAME_1, TOKEN)});
        when(jwsValidator.validate(TOKEN)).thenReturn(TokenInfo.valid(TOKEN, UserType.INTERNAL, claims, USER_ID));

        TokenInfo tokenInfo = tokenFinder.getIngressTokenInfo(request, false);

        assertTrue(tokenInfo.isValid());
    }

    @Test
    void when_bothValidAndInvalidTokensSupplied_then_getIngressTokenInfo_picksValidToken() {
        when(request.getSession()).thenReturn(session);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + INVALID_TOKEN);
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie(COOKIE_NAME_2, INVALID_TOKEN),
                new Cookie(COOKIE_NAME_1, TOKEN)});
        when(jwsValidator.validate(TOKEN)).thenReturn(TokenInfo.valid(TOKEN, UserType.EXTERNAL, claims, USER_ID));
        when(jwsValidator.validate(INVALID_TOKEN)).thenReturn(TokenInfo.invalid());

        TokenInfo tokenInfo = tokenFinder.getIngressTokenInfo(request, false);

        assertTrue(tokenInfo.isValid());
    }

    @Test
    void when_noTokenSupplied_then_getIngressTokenInfo_returnsTokenInfoWithStatusInvalid() {
        TokenInfo tokenInfo = tokenFinder.getIngressTokenInfo(request, false);
        assertFalse(tokenInfo.isValid());
    }

    private void arrangePidInQueryString() {
        when(request.getQueryString()).thenReturn("pid=" + USER_ID);
    }

    private void arrangeQueryStringWithoutPid() {
        when(request.getQueryString()).thenReturn("foo=bar");
    }
}
