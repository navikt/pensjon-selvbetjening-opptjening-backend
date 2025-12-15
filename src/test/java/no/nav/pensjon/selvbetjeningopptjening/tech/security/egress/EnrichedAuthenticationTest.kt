package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentertRolle
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.util.function.Function

class EnrichedAuthenticationTest : ShouldSpec({

    should("return access token for given egress service") {
        EnrichedAuthentication(
            initialAuth = mockk(),
            authType = AuthType.PERSON_SELF,
            egressTokenSuppliersByService = tokenSuppliersByService,
            target = RepresentasjonTarget(pid, rolle = RepresentertRolle.SELV)
        ).getEgressAccessToken(
            service = EgressService.PERSONDATA,
            ingressToken = ""
        ).value shouldBe "token1"
    }

    context("getter functions") {
        should("return values from the wrapped class") {
            val initialAuth = mockk<Authentication>().apply {
                every { name } returns "name1"
                every { authorities } returns mutableListOf(GrantedAuthority { "authority1" })
                every { credentials } returns "credentials1"
                every { details } returns "details1"
                every { principal } returns "principal1"
                every { isAuthenticated } returns true
            }

            with(
                EnrichedAuthentication(
                    initialAuth,
                    authType = AuthType.PERSON_SELF,
                    egressTokenSuppliersByService = tokenSuppliersByService,
                    target = RepresentasjonTarget(pid, rolle = RepresentertRolle.SELV)
                )
            ) {
                name shouldBe "name1"
                authorities.first().authority shouldBe "authority1"
                credentials shouldBe "credentials1"
                details shouldBe "details1"
                principal shouldBe "principal1"
                isAuthenticated shouldBe true
            }
        }
    }

    context("setAuthenticated") {
        should("set the 'isAuthenticated' value in the wrapped class") {
            val initialAuth = mockk<Authentication>().apply {
                every { isAuthenticated = false } returns Unit
            }
            val enrichedAuthentication = EnrichedAuthentication(
                initialAuth = initialAuth,
                authType = AuthType.PERSON_SELF,
                egressTokenSuppliersByService = tokenSuppliersByService,
                target = RepresentasjonTarget(pid, rolle = RepresentertRolle.SELV)
            )
            enrichedAuthentication.isAuthenticated = false

            verify(exactly = 1) { initialAuth.isAuthenticated = false }
        }
    }
})

private val tokenSuppliersByService =
    EgressTokenSuppliersByService(
        value = mapOf(EgressService.PERSONDATA to Function { RawJwt("token1") })
    )
