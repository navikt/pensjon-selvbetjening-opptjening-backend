package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.selvbetjeningopptjening.common.exception.NotFoundException
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group.GroupMembershipService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class ImpersonalAccessFilterTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var chain: FilterChain

    @Mock
    private lateinit var pidExtractor: TargetPidExtractor

    @Mock
    private lateinit var groupMembershipService: GroupMembershipService

    @Mock
    private lateinit var auditor: Auditor

    @Test
    fun `when no fnr in header then doFilter continues filter chain`() {
        `when`(request.getHeader("pid")).thenReturn(null)
        `when`(request.requestURI).thenReturn("/api/foo")

        ImpersonalAccessFilter(pidExtractor, groupMembershipService, auditor).doFilter(request, response, chain)

        verify(chain, times(1)).doFilter(request, response)
    }

    @Test
    fun `when innlogget bruker mangler gruppemedlemskap then doFilter reports 'forbidden' and breaks filter chain`() {
        `when`(request.getHeader("pid")).thenReturn(pid.pid)
        `when`(pidExtractor.pid()).thenReturn(pid)
        `when`(groupMembershipService.innloggetBrukerHarTilgang(pid)).thenReturn(false)
        `when`(request.requestURI).thenReturn("/api/foo")

        ImpersonalAccessFilter(pidExtractor, groupMembershipService, auditor).doFilter(request, response, chain)

        verify(response, times(1)).sendError(403, "Adgang nektet pga. manglende gruppemedlemskap")
        verify(chain, never()).doFilter(request, response)
    }

    @Test
    fun `when person not found then doFilter reports 'not found' and breaks filter chain`() {
        `when`(request.getHeader("pid")).thenReturn(pid.pid)
        `when`(pidExtractor.pid()).thenReturn(pid)
        `when`(groupMembershipService.innloggetBrukerHarTilgang(pid)).thenThrow(NotFoundException("person"))
        `when`(request.requestURI).thenReturn("/api/foo")

        ImpersonalAccessFilter(pidExtractor, groupMembershipService, auditor).doFilter(request, response, chain)

        verify(response, times(1)).sendError(404, "Person ikke funnet")
        verify(chain, never()).doFilter(request, response)
    }

    @Test
    fun `when innlogget bruker har tilgang then audit info is logged and filter chain continues`() {
        `when`(request.getHeader("pid")).thenReturn(pid.pid)
        `when`(request.requestURI).thenReturn("/foo")
        `when`(pidExtractor.pid()).thenReturn(pid)
        `when`(groupMembershipService.innloggetBrukerHarTilgang(pid)).thenReturn(true)

        ImpersonalAccessFilter(pidExtractor, groupMembershipService, auditor).doFilter(request, response, chain)

        verify(auditor, times(1)).audit(pid, "/foo")
        verify(chain, times(1)).doFilter(request, response)
    }

    @Test
    fun `if 'feature' request then access check is skipped and filter chain continues`() {
        `when`(request.requestURI).thenReturn("/api/feature/foo")

        ImpersonalAccessFilter(pidExtractor, groupMembershipService, auditor).doFilter(request, response, chain)

        verify(pidExtractor, never()).pid()
        verify(chain, times(1)).doFilter(request, response)
    }
}
