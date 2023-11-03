package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.FullmaktFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSpec;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

@Component
public class RequestBasedBrukerbytte {

    private static final Logger log = LoggerFactory.getLogger(RequestBasedBrukerbytte.class);
    private final FullmaktFacade fullmaktFacade;
    private final CookieSetter cookieSetter;
    private final Auditor auditor;

    public RequestBasedBrukerbytte(FullmaktFacade fullmaktFacade, CookieSetter cookieSetter, Auditor auditor) {
        this.fullmaktFacade = fullmaktFacade;
        this.cookieSetter = cookieSetter;
        this.auditor = auditor;
    }

    public void byttBruker(HttpServletRequest request,
                           TokenInfo ingressTokenInfo,
                           EgressTokenSupplier egressTokenSupplier,
                           HttpServletResponse response) throws IOException {
        String requestJson = getJson(request.getReader());
        String fullmektigPid = getFullmektigPid(requestJson, ingressTokenInfo);
        try (RequestContext ignored = getUserContextForCheckingPermission(fullmektigPid, ingressTokenInfo, egressTokenSupplier)) {
            byttBruker(fullmektigPid, requestJson, response);
            log.info("Request-based brukerbytte performed");
        } catch (IOException e) {
            handleException(e, response);
        }
    }

    private String getFullmektigPid(String requestJson, TokenInfo ingressTokenInfo) throws IOException {
        UserType userType = ingressTokenInfo.getUserType();

        switch (userType) {
            case EXTERNAL:
                return ingressTokenInfo.getUserId();
            case INTERNAL:
                return getVirtualLoggedInUserPid(requestJson);
            default:
                log.error("Unexpected user type: {}", userType);
                return null;
        }
    }

    private RequestContext getUserContextForCheckingPermission(String fullmektigPid,
                                                            TokenInfo ingressTokenInfo,
                                                            EgressTokenSupplier egressTokenSupplier) {
        UserType userType = ingressTokenInfo.getUserType();

        switch (userType) {
            case EXTERNAL:
                return RequestContext.forExternalUser(ingressTokenInfo, egressTokenSupplier);
            case INTERNAL:
                return RequestContext.forInternalUser(ingressTokenInfo, fullmektigPid, egressTokenSupplier);
            default:
                log.error("Unexpected user type: {}", userType);
                return null;
        }
    }

    private void byttBruker(String fullmektigPid,
                            String requestJson,
                            HttpServletResponse response) throws IOException {
        log.debug("Received request for brukerbytte");
        String onBehalfOfPid = getOnBehalfOfPid(requestJson);
        if (onBehalfOfPid.equals(fullmektigPid)) {
            log.info("Request for tilbakebrukerbytte accepted");
            cookieSetter.unsetCookie(response, CookieType.ON_BEHALF_OF_PID);
            respondBrukerbytteOk(response);
            return;
        }

        boolean mayActOnBehalf = fullmaktFacade.mayActOnBehalfOf(onBehalfOfPid, fullmektigPid);

        if (mayActOnBehalf) {
            log.info("Request for brukerbytte accepted");
            cookieSetter.setCookies(response, List.of(new CookieSpec(CookieType.ON_BEHALF_OF_PID, onBehalfOfPid)));
            respondBrukerbytteOk(response);
            auditor.auditFullmakt(fullmektigPid, onBehalfOfPid);
            return;
        }

        respondWithError(HttpStatus.FORBIDDEN, "ingen fullmakt", response);
    }

    protected String getJson(Reader reader) throws IOException {
        return IOUtils.toString(reader);
    }

    private String getOnBehalfOfPid(String requestJson) throws IOException {
        return new ObjectMapper().readValue(requestJson, ByttBrukerRequest.class).fullmaktsgiverPid;
    }

    private String getVirtualLoggedInUserPid(String requestJson) throws IOException {
        return new ObjectMapper().readValue(requestJson, ByttBrukerRequest.class).fullmektigPid;
    }

    private static void respondBrukerbytteOk(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());

        try (Writer writer = response.getWriter()) {
            writer.write("{ \"result\": true }");
            writer.flush();
        }
    }

    private static void respondWithError(HttpStatus status, String reason, HttpServletResponse response) throws IOException {
        log.info("Request for brukerbytte denied ({})", reason);
        response.sendError(status.value());
    }

    private static void handleException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Brukerbytte FAILED", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
