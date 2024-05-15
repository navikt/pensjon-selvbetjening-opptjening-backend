package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Filter05RequestContextTest extends FilterTest {

    private static final String VIRTUAL_LOGGED_IN_PID = "02034567890";
    private static final String ON_BEHALF_OF_PID = "01023456789"; // fullmaktsgiver
    private static final String REQUEST_URI = "api/send";
    private Filter05RequestContext filter;

    @Mock
    private CookieBasedBrukerbytte brukerbytte;

    @BeforeEach
    void initialize() {
        filter = new Filter05RequestContext(brukerbytte);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void when_requestIsForUnprotectedResource_then_doFilter_continuesFilterChain_and_doesNotCreateRequestContext() throws Exception {
        when(request.getRequestURI()).thenReturn("/internal/alive");
        arrangeFilterChainData(FilterChainData.instanceWhenRequestIsForUnprotectedResource());
        doAnswer(invocation -> assertRequestContextNotCreated()).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    @Test
    void when_accessByApplication_then_doFilter_continuesFilterChain_and_doesNotCreateRequestContext() throws Exception {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
        arrangeFilterChainData(FilterChainData.defaultInstance().withAccessAsApplication(true));
        doAnswer(invocation -> assertRequestContextNotCreated()).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    @Test
    void when_externalUser_and_fullmakt_then_doFilter_createsRequestContextWithFullmaktsgiverAsTarget_and_continuesFilterChain() throws Exception {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
        arrangeValidImpersonalTokens(AppIds.PENSJONSFAGLIG_KJERNE.appName);
        arrangeFullmakt();
        doAnswer(invocation -> {
            assertFalse(RequestContext.userIsInternal());
            assertEquals(ON_BEHALF_OF_PID, RequestContext.getSubjectPid());
            assertEquals(VALID_TOKEN, RequestContext.getEgressAccessToken(AppIds.PENSJONSFAGLIG_KJERNE).getValue());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    private void arrangeFullmakt() {
        when(brukerbytte.getFullmaktsgiverPid(eq(request), any(), any())).thenReturn(ON_BEHALF_OF_PID);
    }

    @Test
    void when_externalUser_and_fullmakt_for_service_that_requires_impersonal_token_should_use_it_and_continuesFilterChain() throws Exception {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
        arrangeValidImpersonalTokens(AppIds.PENSJONSFAGLIG_KJERNE.appName);
        arrangeFullmakt();
        doAnswer(invocation -> {
            assertFalse(RequestContext.userIsInternal());
            assertEquals(ON_BEHALF_OF_PID, RequestContext.getSubjectPid());
            assertEquals(VALID_TOKEN, RequestContext.getEgressAccessToken(AppIds.PENSJONSFAGLIG_KJERNE).getValue());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    @Test
    void when_internalUser_and_noVirtualLoggedInPidInSession_then_doFilter_respondsWithBadRequest_and_cutsFilterChain() throws Exception {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
        arrangeValidPersonalTokens();

        filter.doFilter(request, response, filterChain);

        assertBadRequest();
        assertCutFilterChain();
    }

    @Test
    void when_internalUser_and_validTokens_then_doFilter_createsRequestContextWithVirtualPidAsTarget_and_continuesFilterChain() throws Exception {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
        arrangeValidPersonalTokens();
        arrangeVirtualUser();
        doAnswer(invocation -> assertInternalRequestContextPopulated(VIRTUAL_LOGGED_IN_PID)).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    @Test
    void when_internalUser_and_fullmakt_then_doFilter_createsRequestContextWithVirtualPidAsTarget_and_continuesFilterChain() throws Exception {
        arrangeValidPersonalTokens();
        arrangeVirtualUser();
        arrangeFullmakt();
        doAnswer(invocation -> assertInternalRequestContextPopulated(VIRTUAL_LOGGED_IN_PID)).when(filterChain).doFilter(request, response);
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    private void assertBadRequest() throws Exception {
        verify(response, times(1)).sendError(HttpStatus.BAD_REQUEST.value());
    }

    private void arrangeValidPersonalTokens() {
        arrangeFilterChainData(FilterChainData.defaultInstance()
                .withIngressTokenInfo(validIngressTokenInfo(UserType.INTERNAL))
                .withPersonalEgressTokenSuppliersByApp(egressTokenSuppliersByApp(AppIds.PENSJONSFAGLIG_KJERNE)));
    }

    private void arrangeValidImpersonalTokens(String appIdName) {
        arrangeFilterChainData(FilterChainData.defaultInstance()
                .withIngressTokenInfo(validIngressTokenInfo(UserType.EXTERNAL))
                .withImpersonalEgressTokenSuppliersByApp(egressTokenSuppliersByAppWithClientCredentials(appIdName)));
    }

    private void arrangeVirtualUser() {
        when(request.getQueryString()).thenReturn("fnr=" + VIRTUAL_LOGGED_IN_PID);
    }

    private TokenInfo validIngressTokenInfo(UserType userType) {
        return TokenInfo.valid(VALID_TOKEN, userType, claims, USER_ID);
    }

    private static Object assertRequestContextNotCreated() {
        var exception = assertThrows(SecurityException.class, RequestContext::getSubjectPid);
        assertEquals("No user context", exception.getMessage());
        return null;
    }

    private static Object assertInternalRequestContextPopulated(String expectedPid) {
        assertRequestContextPopulated(expectedPid);
        return null;
    }

    private static void assertRequestContextPopulated(String expectedPid) {
        assertTrue(RequestContext.userIsInternal());
        assertEquals(expectedPid, RequestContext.getSubjectPid());
        assertEquals(VALID_TOKEN, RequestContext.getEgressAccessToken(AppIds.PENSJONSFAGLIG_KJERNE).getValue());
    }
}
