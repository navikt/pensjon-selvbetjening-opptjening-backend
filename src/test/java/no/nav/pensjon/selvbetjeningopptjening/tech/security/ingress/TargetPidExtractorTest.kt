package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class TargetPidExtractorTest {

    @Test
    fun `'pid' function throws PidValidationException if no PID in security context`() {
        SecurityContextHolder.setContext(SecurityContextImpl(null))
        val exception = assertThrows(PidValidationException::class.java) { TargetPidExtractor().pid() }
        assertEquals("Pid validation failed: No PID found", exception.message)
    }
}
