package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group

import no.nav.pensjon.selvbetjeningopptjening.mock.MockAuthentication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextGroupServiceTest {

    @Test
    fun `groups extracts groups from JWT in security context`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("groups", listOf("group1", "group2"))))

        val groups = SecurityContextGroupService().groups()

        assertEquals(2, groups.size)
        assertEquals("group1", groups[0])
        assertEquals("group2", groups[1])
    }
}
