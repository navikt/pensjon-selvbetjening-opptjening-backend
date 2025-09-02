package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit

import no.nav.pensjon.selvbetjeningopptjening.mock.MockAuthentication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextNavIdExtractorTest {

    @Test
    fun `id returns Nav identifier if found in JWT 'NAVident' claim`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("NAVident", "X123456")))
        assertEquals("X123456", SecurityContextNavIdExtractor().id())
    }

    @Test
    fun `id returns empty string if no 'NAVident' claim in JWT`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("other-ident", "id1")))
        assertEquals("", SecurityContextNavIdExtractor().id())
    }
}
