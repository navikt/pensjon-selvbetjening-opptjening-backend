package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.usersession.Logout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class Filter00UnprotectedResourcesTest extends FilterTest {

    private static final String APP_ID = "app1";
    private static final String AUDIENCE = "audience1";
    private Filter00UnprotectedResources filter;

    @Mock
    private Logout logout;
    @Mock
    private PrintWriter writer;

    @BeforeEach
    void initialize() throws IOException {
        filter = new Filter00UnprotectedResources(
                ingressTokenFinder,
                egressAccessTokenFacade,
                tokenAudiencesVsApps(),
                logout);

        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void when_requestingLiveness_then_doFilter_cutsFilterChain_and_respondsWithAlive() throws Exception {
        arrangeRequest("/internal/alive");

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertRequestAttributeNotSet();
        assertResponseStatus(HttpStatus.OK);
        assertResponseBody("Alive");
    }

    @Test
    void when_requestingReadiness_then_doFilter_cutsFilterChain_and_respondsWithReady() throws Exception {
        arrangeRequest("/internal/ready");

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertRequestAttributeNotSet();
        assertResponseStatus(HttpStatus.OK);
        assertResponseBody("Ready");
    }

    @Test
    void when_requestingStatus_then_doFilter_cutsFilterChain_and_respondsWithStatusJson() throws Exception {
        arrangeRequest("/api/status");

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertRequestAttributeNotSet();
        assertResponseStatus(HttpStatus.OK);
        assertResponseBody("{ \"status\": \"OK\" }");
    }

    @Test
    void when_pinging_then_doFilter_cutsFilterChain_and_respondsWithJsonOk() throws Exception {
        arrangeRequest("/internal/ping");

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertRequestAttributeNotSet();
        assertResponseStatus(HttpStatus.OK);
        assertResponseBody("pong");
    }

    @Test
    void when_requestingFavicon_then_doFilter_cutsFilterChain_and_respondsWithNoContent() throws Exception {
        arrangeRequest("/favicon.ico");

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertRequestAttributeNotSet();
        assertResponseStatus(HttpStatus.NO_CONTENT);
        assertNoContent();
    }

    @Test
    void when_requestingLogout_then_doFilter_callsLogout_and_continuesFilterChain() throws Exception {
        arrangeRequest("/logout");
        TokenInfo ingressTokenInfo = arrangeIngressToken();
        User user = new User(ingressTokenInfo.getUserId(), ingressTokenInfo.getUserType());
        when(request.getSession()).thenReturn(session);
        filter.doFilter(request, response, filterChain);

        verify(logout, times(1)).perform(response, user, Set.of(AUDIENCE));
        assertRequestAttributeSet();
        assertContinueFilterChain();
    }

    @Test
    void when_requestingProtectedResource_then_doFilter_continuesFilterChain_and_doesNotSetRequestAttribute() throws Exception {
        arrangeRequest("/protected"); // any URL that is not listed as unprotected

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
        assertRequestAttributeNotSet();
    }

    private void arrangeRequest(String uri) {
        when(request.getRequestURI()).thenReturn(uri);
    }

    private TokenInfo arrangeIngressToken() {
        var tokenInfo = TokenInfo.valid(VALID_TOKEN, UserType.EXTERNAL, null, USER_ID);
        when(ingressTokenFinder.getIngressTokenInfo(request, true)).thenReturn(tokenInfo);
        return tokenInfo;
    }

    private void assertResponseBody(String body) {
        verify(writer, times(1)).write(body);
        verify(writer, times(1)).close();
    }

    private void assertRequestAttributeSet() {
        verify(request, times(1)).setAttribute(eq(FILTER_CHAIN_DATA_ATTRIBUTE_NAME), chainDataCaptor.capture());
        assertTrue(chainDataCaptor.getValue().requestIsForUnprotectedResource());
    }

    private void assertRequestAttributeNotSet() {
        verify(request, never()).setAttribute(any(), any());
    }

    private void assertNoContent() {
        verify(writer, never()).write(anyString());
    }

    private static TokenAudiencesVsApps tokenAudiencesVsApps() {
        return new TokenAudiencesVsApps(Map.of(AUDIENCE, List.of(APP_ID)));
    }
}
