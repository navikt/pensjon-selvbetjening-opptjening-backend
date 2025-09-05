package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import no.nav.pensjon.selvbetjeningopptjening.mock.MockAuthentication
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextPidExtractorTest {

    @Test
    fun `pid returns PID if found in JWT 'pid' claim`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("pid", pid.pid)))
        assertEquals(pid, SecurityContextPidExtractor().pid())
    }

    @Test
    fun `pid returns null if no 'pid' claim in JWT`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("no-pid", pid.pid)))
        assertNull(SecurityContextPidExtractor().pid())
    }
}
