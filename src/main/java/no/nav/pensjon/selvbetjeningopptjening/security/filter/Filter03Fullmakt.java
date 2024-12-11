package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.*;

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
    private final CookieBasedBrukerbytte cookieBased;

    public Filter03Fullmakt(CookieBasedBrukerbytte cookieBased) {
        this.cookieBased = cookieBased;
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

        String fullmaktsgiverPid = cookieBased.getFullmaktsgiverPid(
                (HttpServletRequest) request,
                chainData.ingressTokenInfo(),
                chainData.egressTokenSupplier());

        setAttribute(request, chainData.withFullmaktsgiverPid(fullmaktsgiverPid));
        chain.doFilter(request, response);
    }

}
