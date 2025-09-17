package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentertRolle
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.function.Function

@ExtendWith(SpringExtension::class)
class EnrichedAuthenticationTest {

    private lateinit var enrichedAuthentication: EnrichedAuthentication

    @Mock
    private lateinit var initialAuth: Authentication

    @BeforeEach
    fun initialize() {
        val tokenSuppliersByService =
            EgressTokenSuppliersByService(mapOf(EgressService.PERSONDATA to Function { RawJwt("token1") }))

        enrichedAuthentication =
            EnrichedAuthentication(
                initialAuth,
                authType = AuthType.PERSON_SELF,
                egressTokenSuppliersByService = tokenSuppliersByService,
                target = RepresentasjonTarget(pid, RepresentertRolle.SELV)
            )
    }

    @Test
    fun `getEgressAccessToken returns access token for given egress service`() {
        val token = enrichedAuthentication.getEgressAccessToken(EgressService.PERSONDATA, "")
        assertEquals("token1", token.value)
    }

    @Test
    fun `verify that getters return values from wrapped class`() {
        with(initialAuth) {
            `when`(name).thenReturn("name1")
            `when`(authorities).thenReturn(mutableListOf(GrantedAuthority { "authority1" }))
            `when`(credentials).thenReturn("credentials1")
            `when`(details).thenReturn("details1")
            `when`(principal).thenReturn("principal1")
            `when`(isAuthenticated).thenReturn(true)
        }

        with(enrichedAuthentication) {
            assertEquals("name1", name)
            assertEquals("authority1", authorities.first().authority)
            assertEquals("credentials1", credentials)
            assertEquals("details1", details)
            assertEquals("principal1", principal)
            assertTrue(isAuthenticated)
        }
    }

    @Test
    fun `setAuthenticated sets the value in the wrapped class`() {
        enrichedAuthentication.isAuthenticated = false
        verify(initialAuth, times(1)).isAuthenticated = false
    }
}
