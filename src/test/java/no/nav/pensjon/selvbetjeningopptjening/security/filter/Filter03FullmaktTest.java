package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class Filter03FullmaktTest extends FilterTest {

    private Filter03Fullmakt filter;

    @Mock
    private CookieBasedBrukerbytte brukerbytte;

    @BeforeEach
    void initialize() {
        filter = new Filter03Fullmakt(brukerbytte);
    }

    @Test
    void doFilter_checksForCookieBasedBrukerbytte_and_continuesFilterChain() throws Exception {
        TokenInfo ingressTokenInfo = validIngressTokenInfo();
        Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp = egressTokenSuppliersByApp();
        arrangeValidTokens(ingressTokenInfo, egressTokenSuppliersByApp);

        filter.doFilter(request, response, filterChain);

        verify(brukerbytte, times(1)).getFullmaktsgiverPid(request, ingressTokenInfo, EgressTokenSupplier.forInternalUser(egressTokenSuppliersByApp));
        assertContinueFilterChain();
    }

    @Test
    void when_requestForUnprotectedResource_then_doFilter_skipsChecksForBrukerbytte_and_continuesFilterChain() throws Exception {
        arrangeUnprotectedRequest();

        filter.doFilter(request, response, filterChain);

        verify(brukerbytte, never()).getFullmaktsgiverPid(any(), any(), any());
        assertContinueFilterChain();
    }

    @Test
    void when_runtimeExceptionDuringCheckingForCookieBasedBrukerbytte_then_doFilter_cutsFilterChain() throws Exception {
        TokenInfo ingressTokenInfo = validIngressTokenInfo();
        Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp = egressTokenSuppliersByApp();
        arrangeValidTokens(ingressTokenInfo, egressTokenSuppliersByApp);
        doThrow(new RuntimeException("oops!")).when(brukerbytte).getFullmaktsgiverPid(request, ingressTokenInfo, EgressTokenSupplier.forInternalUser(egressTokenSuppliersByApp));

        var exception = assertThrows(RuntimeException.class, () -> filter.doFilter(request, response, filterChain));

        assertCutFilterChain();
        assertEquals("oops!", exception.getMessage());
    }

    private void arrangeValidTokens(TokenInfo ingressTokenInfo, Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp) {
        arrangeFilterChainData(FilterChainData.defaultInstance()
                .withIngressTokenInfo(ingressTokenInfo)
                .withPersonalEgressTokenSuppliersByApp(egressTokenSuppliersByApp));
    }
}
