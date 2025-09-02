package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt

import no.nav.pensjon.selvbetjeningopptjening.mock.MockAuthentication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextClaimExtractorTest {

    @Test
    fun `claim extracts claim from JWT in security context`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("key1", "value1")))
        val value = SecurityContextClaimExtractor.claim("key1")
        assertEquals("value1", value)
    }
}
