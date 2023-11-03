package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.QueryStringParser;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser.QueryParamNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.getAttribute;
import static org.springframework.util.StringUtils.hasText;

/**
 * Creates a request context and assigns it to the current thread (as a ThreadLocal).
 * This allows the user's authorization info to be accessed when needed during execution of the request.
 */
@Component
@Order(5)
public class Filter05RequestContext implements Filter {

    private static final Logger log = LoggerFactory.getLogger(Filter05RequestContext.class);
    private final CookieBasedBrukerbytte brukerbytte;

    public Filter05RequestContext(CookieBasedBrukerbytte brukerbytte) {
        this.brukerbytte = brukerbytte;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        FilterChainData chainData = getAttribute(request);
        var httpRequest = (HttpServletRequest) request;

        if (chainData.requestIsForUnprotectedResource() || chainData.accessIsAsApplication()) {
            chain.doFilter(request, response);
            return;
        }

        TokenInfo ingressTokenInfo = chainData.ingressTokenInfo();
        UserType userType = ingressTokenInfo.getUserType();

        if (UserType.EXTERNAL.equals(userType)) {
            handleExternalUser(httpRequest, response, chain, ingressTokenInfo, chainData);
            return;
        }

        if (UserType.INTERNAL.equals(userType)) {
            String virtualLoggedInUserPid = getVirtualLoggedInPid(httpRequest);

            if (hasText(virtualLoggedInUserPid)) {
                handleInternalUser(httpRequest, response, chain,
                        ingressTokenInfo, chainData.egressTokenSupplier(), virtualLoggedInUserPid);
                return;
            }

            log.info("FAILED to process request by internal user, since no virtual logged-in user PID in session");
            respondWithBadRequest((HttpServletResponse) response);
        }
    }

    private void handleExternalUser(HttpServletRequest request,
                                    ServletResponse response,
                                    FilterChain chain,
                                    TokenInfo ingressTokenInfo,
                                    FilterChainData chainData) throws IOException, ServletException {
        EgressTokenSupplier egressTokenSupplier = chainData.egressTokenSupplier();
        String onBehalfOfPid = brukerbytte.getFullmaktsgiverPid(request, ingressTokenInfo, egressTokenSupplier);

        if (hasText(onBehalfOfPid)) {
            try (RequestContext ignored = RequestContext.forExternalUserOnBehalf(
                    ingressTokenInfo, onBehalfOfPid, egressTokenSupplier)) {
                log.debug("Processing request for user on behalf of fullmaktsgiver");
                chain.doFilter(request, response);
            }
        } else {
            try (RequestContext ignored = RequestContext.forExternalUser(ingressTokenInfo, egressTokenSupplier)) {
                chain.doFilter(request, response);
            }
        }
    }

    private void handleInternalUser(HttpServletRequest request,
                                    ServletResponse response,
                                    FilterChain chain,
                                    TokenInfo ingressTokenInfo,
                                    EgressTokenSupplier egressTokenSupplier,
                                    String virtualLoggedInUserPid) throws IOException, ServletException {
        String onBehalfOfPid = brukerbytte.getFullmaktsgiverPid(request, ingressTokenInfo, egressTokenSupplier);

        if (hasText(onBehalfOfPid)) {
            try (RequestContext ignored = RequestContext.forInternalUserOnBehalf(
                    ingressTokenInfo, virtualLoggedInUserPid, onBehalfOfPid, egressTokenSupplier)) {
                log.debug("Processing request for virtual logged-in user on behalf of fullmaktsgiver");
                chain.doFilter(request, response);
            }
        } else {
            try (RequestContext ignored = RequestContext.forInternalUser(
                    ingressTokenInfo, virtualLoggedInUserPid, egressTokenSupplier)) {
                log.debug("Processing request for virtual logged-in user");
                chain.doFilter(request, response);
            }
        }
    }

    private static String getVirtualLoggedInPid(HttpServletRequest request) {
        return QueryStringParser.getValue(request.getQueryString(), QueryParamNames.PID);
    }

    private static void respondWithBadRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
