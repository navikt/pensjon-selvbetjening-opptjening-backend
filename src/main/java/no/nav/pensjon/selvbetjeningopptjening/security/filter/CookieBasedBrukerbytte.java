package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.FullmaktFacade;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.QueryStringParser;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser.QueryParamNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static java.util.Arrays.stream;
import static org.springframework.util.StringUtils.hasText;

@Component
public class CookieBasedBrukerbytte {

    private static final String ON_BEHALF_OF_COOKIE_NAME = "nav-obo";
    private static final Logger log = LoggerFactory.getLogger(CookieBasedBrukerbytte.class);
    private final FullmaktFacade fullmaktFacade;
    private final Auditor auditor;

    public CookieBasedBrukerbytte(FullmaktFacade fullmaktFacade, Auditor auditor) {
        this.fullmaktFacade = fullmaktFacade;
        this.auditor = auditor;
    }

    public String getFullmaktsgiverPid(HttpServletRequest request,
                                       TokenInfo ingressTokenInfo,
                                       EgressTokenSupplier egressTokenSupplier) {
        log.debug("Checking need for brukerbytte");
        boolean isInternalUser = UserType.INTERNAL.equals(ingressTokenInfo.getUserType());

        if (isInternalUser) {
            return "";
        }

        String userId = ingressTokenInfo.getUserId();
        String newFullmaktsgiverPidKryptert = getOnBehalfOfPid(request.getCookies());

        RepresentasjonValidity representasjonValidity = mayActOnBehalf(ingressTokenInfo, egressTokenSupplier, userId, newFullmaktsgiverPidKryptert);

        boolean actingOnOwnBehalf = !hasText(newFullmaktsgiverPidKryptert) || userId.equals(representasjonValidity.fullmaktsgiverFnr());

        if (actingOnOwnBehalf) {
            log.info("Request for brukertilbakebytte accepted");
            return "";
        }

        if (representasjonValidity.hasValidRepresentasjonsforhold()) {
            log.info("Request for brukerbytte accepted");
            auditor.auditFullmakt(userId, representasjonValidity.fullmaktsgiverFnr());

            return representasjonValidity.fullmaktsgiverFnr();
        }

        log.info("Request for brukerbytte DENIED");
        return "";
    }

    private RepresentasjonValidity mayActOnBehalf(TokenInfo ingressTokenInfo,
                                                  EgressTokenSupplier egressTokenSupplier,
                                                  String fullmektigPid,
                                                  String fullmaktsgiverPid) {
        try (RequestContext ignored = userContextForCheckingPermission(ingressTokenInfo, egressTokenSupplier)) {
            return fullmaktFacade.mayActOnBehalfOf(fullmaktsgiverPid, fullmektigPid);
        }
    }

    private static String getOnBehalfOfPid(Cookie[] cookies) {
        log.debug("Looking for on-behalf-of PID in cookie...");

        if (cookies == null) {
            return "";
        }

        return stream(cookies)
                .filter(cookie -> ON_BEHALF_OF_COOKIE_NAME.equalsIgnoreCase(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }

    private static RequestContext userContextForCheckingPermission(TokenInfo ingressTokenInfo,
                                                                   EgressTokenSupplier egressTokenSupplier) {
        switch (ingressTokenInfo.getUserType()) {
            case EXTERNAL:
                return RequestContext.forExternalUser(ingressTokenInfo, egressTokenSupplier);
            default:
                log.error("Unexpected user type: {}", ingressTokenInfo.getUserType());
                return null;
        }
    }
}
