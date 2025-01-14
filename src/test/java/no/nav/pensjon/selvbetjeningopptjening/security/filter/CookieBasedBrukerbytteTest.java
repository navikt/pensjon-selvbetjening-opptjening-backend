package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.FullmaktFacade;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CookieBasedBrukerbytteTest {

    private static final String SAKSBEHANDLER_ID = "saksbehandler-id";
    private static final String FULLMEKTIG_PID = "fullmektig-pid";
    private static final String FULLMAKTSGIVER_PID = "fullmaktsgiver-pid";
    private static final String FULLMAKTSGIVER_PID_KRYPTERT = "fullmaktsgiver-pid-kryptert";
    private CookieBasedBrukerbytte brukerbytte;

    @Mock
    FullmaktFacade fullmaktFacade;
    @Mock
    HttpServletRequest request;
    @Mock
    Claims claims;
    @Mock
    Auditor auditor;

    @BeforeEach
    void initialize() {
        brukerbytte = new CookieBasedBrukerbytte(fullmaktFacade, auditor);
    }

    @Test
    void getFullmaktsgiverPid_audits_and_returns_pid_when_externalUser_and_cookieContainsApprovedFullmaktsgiver() {
        arrangeBrukerbytteCookie(FULLMAKTSGIVER_PID);
        arrangeApprovedFullmaktsgiver();

        String fullmaktsgiverPid = brukerbytte.getFullmaktsgiverPid(request, externalUserTokenInfo(), EgressTokenSupplier.empty());

        assertEquals(FULLMAKTSGIVER_PID, fullmaktsgiverPid);
        verify(auditor, times(1)).auditFullmakt(FULLMEKTIG_PID, FULLMAKTSGIVER_PID);
        verify(auditor, never()).auditInternalUser(any(), any());
    }

    @Test
    void getFullmaktsgiverPid_does_not_return_pid_when_internalUser_and_cookieContainsApprovedFullmaktsgiver() {
        arrangeBrukerbytteCookie(FULLMAKTSGIVER_PID);
        arrangeApprovedFullmaktsgiver();
        arrangeVirtualUser("fnr");

        String fullmaktsgiverPid = brukerbytte.getFullmaktsgiverPid(request, internalUserTokenInfo(), EgressTokenSupplier.empty());

        assertEquals("", fullmaktsgiverPid);
    }

    @Test
    void getFullmaktsgiverPid_returns_emptyString_when_externalUser_and_brukerbytte_is_reverted() {
        arrangeBrukerbytteCookie(FULLMEKTIG_PID); // fullmektig acts "on behalf of" self
        when(fullmaktFacade.mayActOnBehalfOf(FULLMEKTIG_PID)).thenReturn(new RepresentasjonValidity(false, "", "", ""));

        String fullmaktsgiverPid = brukerbytte.getFullmaktsgiverPid(request, externalUserTokenInfo(), EgressTokenSupplier.empty());

        assertEquals("", fullmaktsgiverPid);
        verify(auditor, never()).auditFullmakt(any(), any());
        verify(auditor, never()).auditInternalUser(any(), any());
    }

    @Test
    void getFullmaktsgiverPid_returns_emptyString_when_externalUser_and_noFullmaktsgiverCookie() {

        String fullmaktsgiverPid = brukerbytte.getFullmaktsgiverPid(request, externalUserTokenInfo(), EgressTokenSupplier.empty());

        assertEquals("", fullmaktsgiverPid);
        verify(auditor, never()).auditFullmakt(any(), any());
        verify(auditor, never()).auditInternalUser(any(), any());
    }

    @Test
    void getFullmaktsgiverPid_returns_emptyString_when_internalUser_and_cookieLacksApprovedFullmaktsgiver() {
        arrangeBrukerbytteCookie(FULLMAKTSGIVER_PID);
        arrangeUnapprovedFullmaktsgiver();
        arrangeVirtualUser("fnr");

        String fullmaktsgiverPid = brukerbytte.getFullmaktsgiverPid(request, internalUserTokenInfo(), EgressTokenSupplier.empty());

        assertEquals("", fullmaktsgiverPid);
        verify(auditor, never()).auditFullmakt(any(), any());
        verify(auditor, never()).auditInternalUser(any(), any());
    }

    @Test
    void getFullmaktsgiverPid_returns_emptyString_when_internalUser_and_query_lacks_pid() {
        arrangeBrukerbytteCookie(FULLMAKTSGIVER_PID);
        arrangeApprovedFullmaktsgiver();
        arrangeVirtualUser("not-pid");

        String fullmaktsgiverPid = brukerbytte.getFullmaktsgiverPid(request, internalUserTokenInfo(), EgressTokenSupplier.empty());

        assertEquals("", fullmaktsgiverPid);
        verify(auditor, never()).auditFullmakt(any(), any());
        verify(auditor, never()).auditInternalUser(any(), any());
    }

    private void arrangeBrukerbytteCookie(String pid) {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("nav-obo", pid)});
    }

    private void arrangeApprovedFullmaktsgiver() {
        mayActOnBehalfOf(true);
    }

    private void arrangeUnapprovedFullmaktsgiver() {
        mayActOnBehalfOf(false);
    }

    private void arrangeVirtualUser(String queryParamName) {
        when(request.getQueryString()).thenReturn(queryParamName + "=" + FULLMEKTIG_PID);
    }

    private void mayActOnBehalfOf(boolean value) {
        when(fullmaktFacade.mayActOnBehalfOf(FULLMAKTSGIVER_PID)).thenReturn(new RepresentasjonValidity(value, "", "", "fullmaktsgiver-pid"));
    }

    private TokenInfo externalUserTokenInfo() {
        return TokenInfo.valid("jwt", UserType.EXTERNAL, claims, FULLMEKTIG_PID);
    }

    private TokenInfo internalUserTokenInfo() {
        return TokenInfo.valid("jwt", UserType.INTERNAL, claims, SAKSBEHANDLER_ID);
    }
}
