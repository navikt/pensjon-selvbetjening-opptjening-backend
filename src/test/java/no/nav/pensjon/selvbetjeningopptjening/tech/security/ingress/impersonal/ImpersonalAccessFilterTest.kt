package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.selvbetjeningopptjening.common.exception.NotFoundException
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group.GroupMembershipService

class ImpersonalAccessFilterTest : ShouldSpec({

    should("continue filter chain when no fnr in header") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = null, uri = "/api/foo")
        val response = mockk<ServletResponse>()

        ImpersonalAccessFilter(
            pidGetter = mockk(),
            groupMembershipService = mockk(),
            auditor = mockk()
        ).doFilter(request, response, chain)

        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when innlogget bruker mangler gruppemedlemskap") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.pid, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeTilgang(false),
            auditor = mockk()
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(403, "Adgang nektet pga. manglende gruppemedlemskap") }
        verify { chain wasNot Called }
    }

    should("report 'not found' and break filter chain when person not found") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.pid, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeMissingPerson(),
            auditor = mockk()
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(404, "Person ikke funnet") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("log audit info and continue filter chain when innlogget bruker har tilgang") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.pid, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeTilgang(true),
            auditor
        ).doFilter(request, response, chain)

        verify(exactly = 1) { auditor.audit(pid, "/foo") }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }
})

private fun arrangePid(): TargetPidExtractor =
    mockk<TargetPidExtractor>().apply {
        every { pid() } returns pid
    }

private fun arrangeRequest(pid: String?, uri: String): HttpServletRequest =
    mockk<HttpServletRequest>().apply {
        every { getHeader("pid") } returns pid
        every { getParameter("pid") } returns pid
        every { requestURI } returns uri
    }

private fun arrangeTilgang(harTilgang: Boolean): GroupMembershipService =
    mockk<GroupMembershipService>().apply {
        every { innloggetBrukerHarTilgang(pid) } returns harTilgang
    }

private fun arrangeMissingPerson(): GroupMembershipService =
    mockk<GroupMembershipService>().apply {
        every { innloggetBrukerHarTilgang(pid) } throws NotFoundException("person")
    }
