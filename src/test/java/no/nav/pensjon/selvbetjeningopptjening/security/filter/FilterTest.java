package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

/**
 * Base class for tests involving FilterChain.
 */
@ExtendWith(SpringExtension.class)
public class FilterTest {

    protected static final String FILTER_CHAIN_DATA_ATTRIBUTE_NAME = "FilterChainData";
    protected static final String VALID_TOKEN = "j.w.t";
    protected static final String USER_ID = "user1";
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    @Mock
    HttpSession session;
    @Mock
    Claims claims;
    @Mock
    IngressTokenFinder ingressTokenFinder;
    @Mock
    EgressAccessTokenFacade egressAccessTokenFacade;

    @Captor
    ArgumentCaptor<FilterChainData> chainDataCaptor;

    protected void arrangeFilterChainData(FilterChainData data) {
        when(request.getAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME)).thenReturn(data);
    }

    protected void assertContinueFilterChain() throws Exception {
        verify(filterChain, times(1)).doFilter(request, response);
    }

    protected void assertCutFilterChain() throws Exception {
        verify(filterChain, never()).doFilter(request, response);
    }

    protected void assertResponseStatus(HttpStatus status) {
        verify(response, times(1)).setStatus(status.value());
    }

    protected void arrangeUnprotectedRequest() {
        when(request.getAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME))
                .thenReturn(FilterChainData.instanceWhenRequestIsForUnprotectedResource());
    }

    protected TokenInfo validIngressTokenInfo() {
        return TokenInfo.valid(VALID_TOKEN, UserType.INTERNAL, claims, USER_ID);
    }

    protected static Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp() {
        return egressTokenSuppliersByApp(AppIds.PENSJONSFAGLIG_KJERNE);
    }

    protected static Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp(AppIds service) {
        return Map.of(service.appName, () -> new RawJwt(VALID_TOKEN));
    }

    protected static Map<String, Supplier<RawJwt>> egressTokenSuppliersByAppWithClientCredentials(String appIdName) {
        return Map.of(appIdName, () -> new RawJwt(VALID_TOKEN));
    }
}
