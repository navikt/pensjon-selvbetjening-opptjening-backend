package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.FullmaktFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RequestBasedBrukerbytteTest {
    private static final String ON_BEHALF_OF_PID = "01023456789"; // fullmaktsgiver
    private static final String FULLMEKTIG_PID = "02034567890";
    private static final String TOKEN = "j.w.t";
    private static final Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp = Map.of("app1", () -> new RawJwt(TOKEN));
    private RequestBasedBrukerbytte brukerbytte;

    @Mock
    private FullmaktFacade fullmaktChecker;
    @Mock
    private CookieSetter cookieSetter;
    @Mock
    private Auditor auditor;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Claims claims;
    @Mock
    private PrintWriter writer;

    @BeforeEach
    void initialize() throws Exception {
        brukerbytte = new TestClass(fullmaktChecker, cookieSetter, auditor);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void when_mayActOnBehalf_then_byttBruker_logsAuditInfo() throws Exception {
        when(fullmaktChecker.mayActOnBehalfOf(ON_BEHALF_OF_PID, FULLMEKTIG_PID)).thenReturn(true);

        brukerbytte.byttBruker(request, tokenInfo(FULLMEKTIG_PID), EgressTokenSupplier.forInternalUser(egressTokenSuppliersByApp), response);

        verify(auditor, times(1)).auditFullmakt(FULLMEKTIG_PID, ON_BEHALF_OF_PID);
    }

    @Test
    void when_mayNotActOnBehalf_then_byttBruker_respondsWithForbidden_and_does_not_set_fullmakt_cookie() throws Exception {
        when(fullmaktChecker.mayActOnBehalfOf(ON_BEHALF_OF_PID, FULLMEKTIG_PID)).thenReturn(false);

        brukerbytte.byttBruker(request, tokenInfo(FULLMEKTIG_PID), EgressTokenSupplier.forInternalUser(egressTokenSuppliersByApp), response);

        assertResponseError(HttpStatus.FORBIDDEN);
        verify(cookieSetter, never()).setCookies(any(), any());
        verify(writer, never()).write(anyString());
        verify(auditor, never()).auditFullmakt(any(), any());
    }

    @Test
    void when_onBehalfOfPid_equals_fullmektigPid_then_byttBruker_removes_fullmakt_cookie() throws Exception {
        brukerbytte.byttBruker(request, tokenInfo(ON_BEHALF_OF_PID), EgressTokenSupplier.forInternalUser(egressTokenSuppliersByApp), response);

        verify(cookieSetter, times(1)).unsetCookie(any(), any());
        assertResponseOk();
        assertResponseBodyWritten();
    }

    @Test
    void when_mayActOnBehalf_then_byttBruker_sets_fullmakt_cookie() throws Exception {
        when(fullmaktChecker.mayActOnBehalfOf(ON_BEHALF_OF_PID, FULLMEKTIG_PID)).thenReturn(true);

        brukerbytte.byttBruker(request, tokenInfo(FULLMEKTIG_PID), EgressTokenSupplier.forInternalUser(egressTokenSuppliersByApp), response);

        verify(cookieSetter, times(1)).setCookies(any(), any());
        assertResponseOk();
        assertResponseBodyWritten();
    }

    private TokenInfo tokenInfo(String pid) {
        return TokenInfo.valid(TOKEN, UserType.EXTERNAL, claims, pid);
    }

    private void assertResponseOk() {
        verify(response, times(1)).setStatus(HttpStatus.OK.value());
    }

    private void assertResponseError(HttpStatus status) throws Exception {
        verify(response, times(1)).sendError(status.value());
    }

    private void assertResponseBodyWritten() {
        verify(writer, times(1)).write("{ \"result\": true }");
        verify(writer, times(1)).close();
    }

    private static class TestClass extends RequestBasedBrukerbytte {

        public TestClass(FullmaktFacade fullmaktFacade, CookieSetter cookieSetter, Auditor auditor) {
            super(fullmaktFacade, cookieSetter, auditor);
        }

        @Override
        protected String getJson(Reader reader) {
            return format(
                    "{\"fullmaktsgiverPid\": \"%s\"}, \"fullmektigPid\": \"%s\"",
                    ON_BEHALF_OF_PID, FULLMEKTIG_PID);
        }
    }
}