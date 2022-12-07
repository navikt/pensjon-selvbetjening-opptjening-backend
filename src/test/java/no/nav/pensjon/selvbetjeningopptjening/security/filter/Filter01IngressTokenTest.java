package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class Filter01IngressTokenTest extends FilterTest {

    private static final String ROLES_CLAIM_KEY = "roles";
    private Filter01IngressToken filter;

    @BeforeEach
    void initialize() {
        filter = new Filter01IngressToken(ingressTokenFinder);
    }

    @Test
    void when_requestIsForUnprotectedResource_then_doFilter_continuesFilterChain_and_doesNotCheckIngressToken() throws Exception {
        when(request.getAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME))
                .thenReturn(FilterChainData.instanceWhenRequestIsForUnprotectedResource());

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
        verify(ingressTokenFinder, never()).getIngressTokenInfo(any(), any(Boolean.class));
    }

    @Test
    void when_ingressTokenIsInvalid_then_doFilter_cutsFilterChain_and_respondsWithUnauthorized() throws Exception {
        arrangeInvalidIngressToken();

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertUnauthorized();
    }

    @Test
    void when_ingressTokenIsValid_then_doFilter_setsRequestAttribute_and_continuesFilterChain() throws Exception {
        arrangeValidIngressToken();

        filter.doFilter(request, response, filterChain);

        verify(request, times(1)).setAttribute(eq(FILTER_CHAIN_DATA_ATTRIBUTE_NAME), chainDataCaptor.capture());
        TokenInfo tokenInfo = chainDataCaptor.getValue().ingressTokenInfo();
        assertTrue(tokenInfo.isValid());
        assertTrue(tokenInfo.hasClaims());
        assertEquals(UserType.EXTERNAL, tokenInfo.getUserType());
        assertEquals(USER_ID, tokenInfo.getUserId());
        assertEquals(VALID_TOKEN, tokenInfo.getJwt());
        assertContinueFilterChain();
    }

    @Test
    void when_ingressTokenIsForAccessByApplication_then_doFilter_signalsSkipEgressTokens_and_continuesFilterChain() throws Exception {
        arrangeAccessAsApplication();
        arrangeValidIngressToken();

        filter.doFilter(request, response, filterChain);

        verify(request, times(1)).setAttribute(eq(FILTER_CHAIN_DATA_ATTRIBUTE_NAME), chainDataCaptor.capture());
        assertTrue(chainDataCaptor.getValue().skipEgressTokens());
        assertContinueFilterChain();
    }

    private void arrangeAccessAsApplication() {
        when(claims.get(ROLES_CLAIM_KEY)).thenReturn(List.of("access_as_application"));
    }

    private void arrangeValidIngressToken() {
        TokenInfo tokenInfo = TokenInfo.valid(VALID_TOKEN, UserType.EXTERNAL, claims, USER_ID);
        arrangeIngressToken(tokenInfo);
    }

    private void arrangeInvalidIngressToken() {
        arrangeIngressToken(TokenInfo.invalid());
    }

    private void arrangeIngressToken(TokenInfo tokenInfo) {
        when(ingressTokenFinder.getIngressTokenInfo(request, false)).thenReturn(tokenInfo);
    }

    private void assertUnauthorized() throws Exception {
        verify(response, times(1)).sendError(HttpStatus.UNAUTHORIZED.value());
    }
}
