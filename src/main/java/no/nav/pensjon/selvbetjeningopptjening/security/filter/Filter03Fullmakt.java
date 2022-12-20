package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.getAttribute;
import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.setAttribute;

/**
 * Handles brukerbytte, i.e. switching of user context.
 * This is relevant when a user has been granted the right to access the application on behalf of another user.
 * The grantee (fullmektig) can then request that the app behave as if the grantor (fullmaktsgiver) is logged in.
 * Brukerbytte can occur via setting a cookie. This allows brukerbytte to be transferred
 * between different applications.
 */
@Component
@Order(3)
public class Filter03Fullmakt implements Filter {

    private static final String ACT_ON_BEHALF_URI = "/api/byttbruker";
    private static final Logger log = LoggerFactory.getLogger(Filter03Fullmakt.class);
    private final CookieBasedBrukerbytte cookieBased;
    private final RequestBasedBrukerbytte requestBased;

    public Filter03Fullmakt(CookieBasedBrukerbytte cookieBased, RequestBasedBrukerbytte requestBased) {
        this.cookieBased = cookieBased;
        this.requestBased = requestBased;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        FilterChainData chainData = getAttribute(request);

        if (chainData.requestIsForUnprotectedResource()) {
            chain.doFilter(request, response);
            return;
        }
        TokenInfo ingressTokenInfo = chainData.ingressTokenInfo();
        EgressTokenSupplier egressTokenSupplier = chainData.egressTokenSupplier();
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        if (isActOnBehalfRequest(httpRequest.getRequestURI())) {
            requestBased.byttBruker(httpRequest, ingressTokenInfo, egressTokenSupplier, httpResponse);
            return;
        }

        String fullmaktsgiverPid = cookieBased.getFullmaktsgiverPid(
                (HttpServletRequest) request,
                chainData.ingressTokenInfo(),
                chainData.egressTokenSupplier());

        setAttribute(request, chainData.withFullmaktsgiverPid(fullmaktsgiverPid));
        chain.doFilter(request, response);
    }

    private static boolean isActOnBehalfRequest(String uri) {
        return ACT_ON_BEHALF_URI.equals(uri) || (ACT_ON_BEHALF_URI + "/").equals(uri);
    }
}
