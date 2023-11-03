package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
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
import static no.nav.pensjon.selvbetjeningopptjening.security.jwt.ClaimsUtil.getGroups;
import static no.nav.pensjon.selvbetjeningopptjening.security.jwt.ClaimsUtil.getInternalUserId;
import static org.springframework.util.StringUtils.hasText;

/**
 * Handles requests when the actual logged-in user is an internal user (NAV employee).
 * The app shall act as if an external user was logged in (i.e. a virtual logged-in user).
 * The PID of the external user must be supplied as a query parameter in the initial request.
 * Security considerations:
 * - The internal user must be a member of a group allowed to access the app
 * - The internal user must be allowed to access the data of the external user ("skjerming")
 */
@Component
@Order(4)
public class Filter04VirtualUser implements Filter {

    private static final Logger log = LoggerFactory.getLogger(Filter04VirtualUser.class);
    private final GroupChecker groupChecker;
    private final Auditor auditor;

    public Filter04VirtualUser(GroupChecker groupChecker, Auditor auditor) {
        this.groupChecker = groupChecker;
        this.auditor = auditor;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        String pid = QueryStringParser.getValue(httpRequest.getQueryString(), QueryParamNames.PID);

        if (!hasText(pid)) {
            chain.doFilter(request, response);
            return;
        }

        FilterChainData chainData = getAttribute(request);
        TokenInfo ingressTokenInfo = chainData.ingressTokenInfo();
        EgressTokenSupplier egressTokenSupplier = chainData.egressTokenSupplier();
        Claims claims = ingressTokenInfo.getClaims();
        var httpResponse = (HttpServletResponse) response;

        if (userLacksRequiredGroupMembership(pid, claims, ingressTokenInfo, egressTokenSupplier)) {
            log.error("Internal user not allowed to access virtual logged-in user");
            httpResponse.sendError(HttpStatus.FORBIDDEN.value());
            return;
        }

        if (chainData.fullmaktEnabled()) {
            String onBehalfOfPid = chainData.fullmaktsgiverPid();

            if (userLacksRequiredGroupMembership(onBehalfOfPid, claims, ingressTokenInfo, egressTokenSupplier)) {
                log.error("Internal user not allowed to access on-behalf-of user (fullmaktsgiver)");
                httpResponse.sendError(HttpStatus.FORBIDDEN.value());
                return;
            }

            auditor.auditInternalUser(getNavIdent(request), onBehalfOfPid);
        }

        auditor.auditInternalUser(getNavIdent(request), pid);
        chain.doFilter(request, response);
    }

    private boolean userLacksRequiredGroupMembership(String pid,
                                                     Claims claims,
                                                     TokenInfo ingressTokenInfo,
                                                     EgressTokenSupplier egressTokenSupplier) {
        try (RequestContext ignored = RequestContext.forInternalUser(ingressTokenInfo, pid, egressTokenSupplier)) {
            return !groupChecker.isUserAuthorized(new Pid(pid), getGroups(claims));
        }
    }

    private static String getNavIdent(ServletRequest request) {
        Claims claims = getAttribute(request).ingressTokenInfo().getClaims();
        return getInternalUserId(claims);
    }
}
