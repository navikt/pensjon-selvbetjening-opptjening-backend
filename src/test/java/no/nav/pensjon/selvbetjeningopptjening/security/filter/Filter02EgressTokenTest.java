package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Filter02EgressTokenTest extends FilterTest {

    private static final String AUDIENCE_1 = "audience1";
    private static final String AUDIENCE_2 = "audience2";
    private static final String PERSONAL_TOKEN = "personal-token";
    private static final String IMPERSONAL_TOKEN = "impersonal-token";
    private Filter02EgressToken filter;

    @Mock
    private TokenAudiencesVsApps audiencesVsApps;

    @BeforeEach
    void initialize() {
        filter = new Filter02EgressToken(egressAccessTokenFacade, audiencesVsApps);
        when(audiencesVsApps.getAppListsByAudience()).thenReturn(appsByAudience());
    }

    @Test
    void when_requestIsForUnprotectedResource_then_doFilter_continuesFilterChain_and_doesNotGetEgressTokens() throws Exception {
        arrangeRequestForUnprotectedResource();

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
        assertNotGettingEgressTokens();
    }

    @Test
    void when_skipEgressTokensSignalled_then_doFilter_continuesFilterChain_and_doesNotGetEgressTokens() throws Exception {
        arrangeSkipEgressTokensSignalled();

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
        assertNotGettingEgressTokens();
    }

    @Test
    void when_gettingEgressTokens_then_doFilter_getsPersonalAndImpersonalTokenSuppliers_and_continuesFilterChain() throws Exception {
        arrangeValidIngressTokenObtained();
        when(egressAccessTokenFacade.getAccessToken(VALID_TOKEN, USER_ID, UserType.EXTERNAL, AUDIENCE_1)).thenReturn(new RawJwt(PERSONAL_TOKEN));
        when(egressAccessTokenFacade.getAccessToken(UserType.APPLICATION, AUDIENCE_1)).thenReturn(new RawJwt(IMPERSONAL_TOKEN));
        doAnswer(this::assertEgressTokens).when(request).setAttribute(eq(FILTER_CHAIN_DATA_ATTRIBUTE_NAME), any(FilterChainData.class));

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
    }

    private void arrangeRequestForUnprotectedResource() {
        arrangeFilterChainData(FilterChainData.instanceWhenRequestIsForUnprotectedResource());
    }

    private void arrangeSkipEgressTokensSignalled() {
        arrangeFilterChainData(FilterChainData.defaultInstance().withSkipEgressTokens(true));
    }

    private void arrangeValidIngressTokenObtained() {
        arrangeFilterChainData(FilterChainData.defaultInstance().withIngressTokenInfo(validTokenInfo()));
    }

    private void assertNotGettingEgressTokens() {
        verify(egressAccessTokenFacade, never()).getAccessToken(any(), any(), any(), any());
    }

    private Object[] assertEgressTokens(InvocationOnMock invocation) {
        var data = (FilterChainData) invocation.getArguments()[1];
        RawJwt personalToken = data.egressTokenSupplier().getPersonalToken(AppIds.PENSJONSFAGLIG_KJERNE);
        RawJwt impersonalToken = data.egressTokenSupplier().getImpersonalToken(AppIds.PENSJONSFAGLIG_KJERNE);
        assertEquals(PERSONAL_TOKEN, personalToken.getValue());
        assertEquals(IMPERSONAL_TOKEN, impersonalToken.getValue());
        return null;
    }

    private TokenInfo validTokenInfo() {
        return TokenInfo.valid(VALID_TOKEN, UserType.EXTERNAL, claims, USER_ID);
    }

    private static Map<String, List<String>> appsByAudience() {
        return Map.of(
                AUDIENCE_1, List.of(AppIds.PENSJONSFAGLIG_KJERNE.appName, "app2"),
                AUDIENCE_2, List.of("app3"));
    }
}
