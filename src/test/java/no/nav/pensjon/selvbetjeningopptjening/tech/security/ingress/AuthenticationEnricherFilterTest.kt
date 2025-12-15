package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.SecurityContextEnricher
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import java.io.PrintWriter

class AuthenticationEnricherFilterTest : ShouldSpec({

    context("access denied") {
        should("set status 403 (Forbidden) and break the filter chain") {
            val request = arrangeRequest()
            val writer = arrangeWriter()
            val response = arrangeForbiddenResponse(writer)
            val chain = arrangeFilterChain(request, response)

            AuthenticationEnricherFilter(
                enricher = arrangeSecurityContextEnricher(request, response)
            ).doFilter(request, response, chain)

            verify(exactly = 1) { response.status = 403 }
            verify(exactly = 1) { response.contentType = MediaType.APPLICATION_JSON_VALUE }
            verify(exactly = 1) { writer.append("""{ "reason": "oops!" }""") }
            verify { chain wasNot called }
        }
    }
})

private fun arrangeSecurityContextEnricher(
    request: HttpServletRequest,
    response: HttpServletResponse
): SecurityContextEnricher =
    mockk<SecurityContextEnricher>().apply {
        every { enrichAuthentication(request, response) } throws AccessDeniedException("oops!")
    }

private fun arrangeFilterChain(request: HttpServletRequest, response: HttpServletResponse): FilterChain =
    mockk<FilterChain>().apply {
        every { doFilter(request, response) } returns Unit
    }

private fun arrangeRequest(uri: String = "/api/foo"): HttpServletRequest =
    mockk<HttpServletRequest>().apply {
        every { requestURI } returns uri
    }

private fun arrangeResponse(printWriter: PrintWriter = mockk()): HttpServletResponse =
    mockk<HttpServletResponse>().apply {
        every { writer } returns printWriter
    }

private fun arrangeForbiddenResponse(printWriter: PrintWriter): HttpServletResponse =
    arrangeResponse(printWriter).apply {
        every { status = 403 } returns Unit
        every { contentType = "application/json" } returns Unit
    }

private fun arrangeWriter(): PrintWriter =
    mockk<PrintWriter>().apply {
        every { append("""{ "reason": "oops!" }""") } returns this
    }
