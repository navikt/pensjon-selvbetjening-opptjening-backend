package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.getAttribute;
import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.setAttribute;

/**
 * Checks for presence of valid ingress (incoming) authorization token (JWT).
 * The token may be supplied as an HTTP header or as a cookie.
 * Both personal and impersonal (access as application) tokens are supported.
 */
@Component
@Order(1)
public class Filter01IngressToken implements Filter {

    private static final String APPLICATION_ACCESS_ROLE = "access_as_application";
    private static final String ROLES_CLAIM_KEY = "roles";
    private static final Logger log = LoggerFactory.getLogger(Filter01IngressToken.class);
    private final IngressTokenFinder ingressTokenFinder;

    public Filter01IngressToken(IngressTokenFinder ingressTokenFinder) {
        this.ingressTokenFinder = ingressTokenFinder;
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

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        TokenInfo ingressTokenInfo = ingressTokenFinder.getIngressTokenInfo(httpRequest, false);

        if (!ingressTokenInfo.isValid()) {
            log.info("Unauthorized (invalid token)");
            unauthorized(httpResponse);
            return;
        }

        if (getRoles(ingressTokenInfo).contains(APPLICATION_ACCESS_ROLE)) {
            log.debug("Access as application");
            setAttribute(request, chainData.withAccessAsApplication(true));
            chain.doFilter(request, response);
            return;
        }

        log.debug("Valid ingress token");
        setAttribute(request, chainData.withIngressTokenInfo(ingressTokenInfo));
        chain.doFilter(request, response);
    }

    private static void unauthorized(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    private static List<?> getRoles(TokenInfo tokenInfo) {
        var roles = (List<?>) tokenInfo.getClaims().get(ROLES_CLAIM_KEY);
        return roles == null ? emptyList() : roles;
    }
}
