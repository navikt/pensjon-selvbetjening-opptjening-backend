package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.usersession.Logout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.setAttribute;
import static no.nav.pensjon.selvbetjeningopptjening.security.filter.Uris.isProtected;

/**
 * Handles requests for resources that are not protected, i.e. they do not require
 * an authorization token in order to be accessed.
 */
@Component
@Order(0)
public class Filter00UnprotectedResources implements Filter {

    private static final String DEV_ENV_URI = "/api/devenv";
    private static final String STATUS_URI = "/api/status";
    private static final String LIVENESS_URI = "/internal/alive";
    private static final String READINESS_URI = "/internal/ready";
    private static final String PING_URI = "/internal/ping";
    private static final String SELF_TEST_URI = "/internal/selftest";
    private static final String LOGOUT_URI = "/logout";
    private static final String FAVICON_URI = "/favicon.ico";
    private static final Logger log = LoggerFactory.getLogger(Filter00UnprotectedResources.class);
    private final IngressTokenFinder ingressTokenFinder;
    private final EgressAccessTokenFacade egressAccessTokenFacade;
    private final TokenAudiencesVsApps audiencesVsApps;
    private final Logout logout;

    public Filter00UnprotectedResources(IngressTokenFinder ingressTokenFinder,
                                        EgressAccessTokenFacade egressAccessTokenFacade,
                                        TokenAudiencesVsApps audiencesVsApps,
                                        Logout logout) {
        this.ingressTokenFinder = ingressTokenFinder;
        this.egressAccessTokenFacade = egressAccessTokenFacade;
        this.audiencesVsApps = audiencesVsApps;
        this.logout = logout;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        if (isProtected(uri)) {
            chain.doFilter(request, response);
            return;
        }

        handleUnprotectedResource(chain, httpRequest, (HttpServletResponse) response);
    }

    private void handleUnprotectedResource(FilterChain chain,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException, ServletException {
        String uri = request.getRequestURI();

        if (LIVENESS_URI.equals(uri)) {
            if (log.isTraceEnabled()) {
                log.trace("Request for liveness: {}", uri);
            }

            reportHealth(response, "Alive");
            return;
        }

        if (READINESS_URI.equals(uri)) {
            if (log.isTraceEnabled()) {
                log.trace("Request for readiness: {}", uri);
            }

            reportHealth(response, "Ready");
            return;
        }

        if (STATUS_URI.equals(uri)) {
            if (log.isTraceEnabled()) {
                log.trace("Request for status: {}", uri);
            }

            reportStatus(response);
            return;
        }

        if (DEV_ENV_URI.equals(uri)) {
            if (log.isTraceEnabled()) {
                log.trace("Request for dev-env: {}", uri);
            }

            reportDevelopmentEnvironment(response);
            return;
        }

        if (PING_URI.equals(uri)) {
            if (log.isTraceEnabled()) {
                log.trace("Request for ping: {}", uri);
            }

            reportHealth(response, "pong");
            return;
        }

        if (FAVICON_URI.equals(uri)) {
            if (log.isTraceEnabled()) {
                log.trace("Request for favicon: {}", uri);
            }

            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }

        if (SELF_TEST_URI.equals(uri)) {
            selfTest(chain, request, response);
            return;
        }

        if (LOGOUT_URI.equals(uri)) {
            if (log.isDebugEnabled()) {
                log.debug("Request for logout: {}", uri);
            }

            logOut(request, response);
        }

        if (log.isTraceEnabled()) {
            log.trace("Request for unprotected resource: {}", uri);
        }

        setAttribute(request, FilterChainData.instanceWhenRequestIsForUnprotectedResource());
        chain.doFilter(request, response);
    }

    private void logOut(HttpServletRequest request, HttpServletResponse response) {
        TokenInfo tokenInfo = ingressTokenFinder.getIngressTokenInfo(request, true);
        Set<String> audiences = audiencesVsApps.getAppListsByAudience().keySet();
        logout.perform(response, getUser(tokenInfo), audiences);
    }

    private void selfTest(FilterChain chain,
                          HttpServletRequest request,
                          ServletResponse response) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("Self-testing...");
        }

        try (RequestContext ignored = userContext()) {
            setAttribute(request, FilterChainData.instanceWhenRequestIsForUnprotectedResource());
            chain.doFilter(request, response);
        }

        log.info("Self-test done");
    }

    private RequestContext userContext() {
        Supplier<RawJwt> tokenSupplier = () -> egressAccessTokenFacade.getAccessToken(UserType.SELF_TEST, "self-test");
        return RequestContext.forSelfTest(TokenInfo.forSelfTest(), EgressTokenSupplier.forApplication(Map.of("self-test", tokenSupplier)));
    }

    private static User getUser(TokenInfo tokenInfo) {
        return tokenInfo == null
                ? new User("", UserType.NONE)
                : new User(tokenInfo.getUserId(), tokenInfo.getUserType());
    }

    private static void reportDevelopmentEnvironment(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());

        String value = Objects.equals(System.getenv("NAIS_CLUSTER_NAME"), "dev-gcp") ?
                System.getenv("AZURE_APP_CLIENT_SECRET") : "forbidden";

        try (Writer writer = response.getWriter()) {
            writer.write(value);
            writer.flush();
        }
    }

    private static void reportHealth(HttpServletResponse response, String body) throws IOException {
        response.setStatus(HttpStatus.OK.value());

        try (Writer writer = response.getWriter()) {
            writer.write(body);
            writer.flush();
        }
    }

    /**
     * Response according to Team Digital Status: Statusplattformen: Metoder for å oppdatere status
     * (https://confluence.adeo.no/pages/viewpage.action?pageId=460442120)
     */
    private static void reportStatus(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try (Writer writer = response.getWriter()) {
            writer.write("""
                    { "status": "OK" }""");
            writer.flush();
        }
    }
}
