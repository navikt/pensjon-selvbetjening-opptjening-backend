package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Filter04VirtualUserTest extends FilterTest {

    private static final String ACTUAL_LOGGED_IN_USER_ID_CLAIM_KEY = "NAVident";
    private static final String GROUPS_CLAIM_KEY = "groups";
    private static final Pid ON_BEHALF_OF_PID = new Pid("01865499538"); // fullmaktsgiver
    private static final Pid VIRTUAL_LOGGED_IN_PID = new Pid("05845997316");
    private static final List<String> groups = List.of("group1", "group2");
    private Filter04VirtualUser filter;

    @Mock
    private GroupChecker groupChecker;
    @Mock
    private Auditor auditor;

    @BeforeEach
    void initialize() {
        filter = new Filter04VirtualUser(groupChecker, auditor);
        when(request.getSession()).thenReturn(session);
        when(claims.get(ACTUAL_LOGGED_IN_USER_ID_CLAIM_KEY)).thenReturn(USER_ID);
        arrangeValidTokens();
    }

    @Test
    void when_noPidInQueryString_then_doFilter_continuesFilterChain_and_doesNotSetSessionAttribute() throws Exception {
        when(request.getQueryString()).thenReturn("foo=bar");

        filter.doFilter(request, response, filterChain);

        assertContinueFilterChain();
        assertSessionAttributeNotSet();
        assertNoAudit();
    }

    @Test
    void when_pidInQueryString_and_userIsAllowedToHandleThatPid_then_doFilter_logsAudit_and_setsSessionAttribute_and_continuesFilterChain() throws Exception {
        arrangePidInQueryString();
        arrangeAllowedToHandleVirtualUser();

        filter.doFilter(request, response, filterChain);

        assertAuditLog(VIRTUAL_LOGGED_IN_PID.getPid());
        assertContinueFilterChain();
    }

    @Test
    void when_userIsNotAllowedToHandleVirtualUser_then_doFilter_cutsFilterChain_and_respondsWithForbidden() throws Exception {
        arrangePidInQueryString();
        arrangeNotAllowedToHandleVirtualUser();

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertForbidden();
        assertSessionAttributeNotSet();
        assertNoAudit();
    }

    @Test
    void when_fullmakt_and_userIsNotAllowedToHandleFullmaktsgiver_then_doFilter_cutsFilterChain_and_respondsWithForbidden() throws Exception {
        arrangeFullmakt();
        arrangePidInQueryString();
        arrangeAllowedToHandleVirtualUser();
        arrangeNotAllowedToHandleOnBehalfOfUser();

        filter.doFilter(request, response, filterChain);

        assertCutFilterChain();
        assertForbidden();
        assertSessionAttributeNotSet();
        assertNoAudit();
    }

    @Test
    void when_fullmakt_and_userIsAllowedToHandleBothParties_then_doFilter_logsAudit_and_continuesFilterChain() throws Exception {
        arrangeFullmakt();
        arrangePidInQueryString();
        arrangeAllowedToHandleVirtualUser();
        when(groupChecker.isUserAuthorized(ON_BEHALF_OF_PID, groups)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertAuditLog(VIRTUAL_LOGGED_IN_PID.getPid());
        assertAuditLog(ON_BEHALF_OF_PID.getPid());
        assertContinueFilterChain();
    }

    private void assertAuditLog(String targetPid) {
        verify(auditor, times(1)).auditInternalUser(USER_ID, targetPid);
    }

    private void arrangePidInQueryString() {
        when(request.getQueryString()).thenReturn("pid=" + VIRTUAL_LOGGED_IN_PID.getPid());
    }

    private void arrangeValidTokens() {
        arrangeFilterChainData(FilterChainData.defaultInstance()
                .withIngressTokenInfo(validIngressTokenInfo())
                .withPersonalEgressTokenSuppliersByApp(egressTokenSuppliersByApp()));
    }

    private void arrangeFullmakt() {
        arrangeFilterChainData(FilterChainData.defaultInstance()
                .withIngressTokenInfo(validIngressTokenInfo())
                .withPersonalEgressTokenSuppliersByApp(egressTokenSuppliersByApp())
                .withFullmaktsgiverPid(ON_BEHALF_OF_PID.getPid()));
    }

    private void arrangeAllowedToHandleVirtualUser() {
        when(claims.get(GROUPS_CLAIM_KEY)).thenReturn(groups);
        when(groupChecker.isUserAuthorized(VIRTUAL_LOGGED_IN_PID, groups)).thenReturn(true);
    }

    private void arrangeNotAllowedToHandleVirtualUser() {
        List<String> noGroups = emptyList();
        when(claims.get(GROUPS_CLAIM_KEY)).thenReturn(noGroups);
        when(groupChecker.isUserAuthorized(VIRTUAL_LOGGED_IN_PID, noGroups)).thenReturn(false);
    }

    private void arrangeNotAllowedToHandleOnBehalfOfUser() {
        when(groupChecker.isUserAuthorized(ON_BEHALF_OF_PID, groups)).thenReturn(false);
    }

    private void assertSessionAttributeNotSet() {
        verify(session, never()).setAttribute(any(), any());
    }

    private void assertForbidden() throws Exception {
        verify(response, times(1)).sendError(HttpStatus.FORBIDDEN.value());
    }

    private void assertNoAudit() {
        verify(auditor, never()).auditFullmakt(any(), any());
    }
}
