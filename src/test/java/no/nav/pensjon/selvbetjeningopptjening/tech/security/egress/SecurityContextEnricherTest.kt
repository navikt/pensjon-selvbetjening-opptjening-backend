package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.crypto.PidEncryptionService
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextEnricherTest : FunSpec({

    val tokenSuppliers = EgressTokenSuppliersByService(emptyMap())

    test("enrichAuthentication uses plaintext PID from header if not encrypted") {
        arrangeSecurityContext(authentication = mockk())
        val securityContextPidExtractor = mockk<SecurityContextPidExtractor>(relaxed = true)

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            securityContextPidExtractor,
            pidDecrypter = mockk(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummerInHeader(LOGGED_IN_PID.pid), // => PID in the request header
            response = mockk()
        )
        verify(exactly = 1) { securityContextPidExtractor.pid() }
        securityContextTargetPid() shouldBe LOGGED_IN_PID
    }

    test("if no PID in request header then enrichAuthentication gets PID from security context") {
        arrangeSecurityContext(authentication = mockk())
        val securityContextPidExtractor = mockk<SecurityContextPidExtractor>(relaxed = true)

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            securityContextPidExtractor,
            pidDecrypter = mockk(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummerInUrl(value = null), // => no PID in the request header
            response = mockk()
        )

        verify(exactly = 2) { securityContextPidExtractor.pid() }
    }

    test("enrichAuthentication decrypts encrypted PID in url") {
        arrangeSecurityContext(authentication = mockk())
        val securityContextPidExtractor = mockk<SecurityContextPidExtractor>(relaxed = true)

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            securityContextPidExtractor,
            pidDecrypter = arrangeDecryption(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummerInUrl("contains.dot"), // encrypted PID
            response = mockk()
        )
        verify(exactly = 1) { securityContextPidExtractor.pid() }
        securityContextTargetPid() shouldBe ON_BEHALF_OF_PID
    }

    test("enrichAuthentication decrypts encrypted PID in header") {
        arrangeSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            pidExtractor = arrangeSecurityContextPidExtractor(pid = null),
            pidDecrypter = arrangeDecryption(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummerInHeader("contains.dot"), // encrypted PID
            response = mockk()
        )

        securityContextTargetPid() shouldBe ON_BEHALF_OF_PID
    }

    test("enrichAuthentication decrypts encrypted PID in cookie") {
        arrangeSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            pidExtractor = arrangeSecurityContextPidExtractor(pid = LOGGED_IN_PID),
            pidDecrypter = arrangeDecryption(),
            representasjonService = arrange(Representasjon(isValid = true, fullmaktGiverNavn = "F. Giver"))
        ).enrichAuthentication(
            request = arrangeOnBehalfOfCookie(value = "contains.dot"), // encrypted PID
            response = mockk()
        )

        securityContextTargetPid() shouldBe ON_BEHALF_OF_PID // decrypted PID
    }

    test("enrichAuthentication uses plaintext PID from url if not encrypted") {
        arrangeSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            pidExtractor = arrangeSecurityContextPidExtractor(pid = null),
            pidDecrypter = mockk(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummerInUrl(ON_BEHALF_OF_PID.pid), // not encrypted
            response = mockk()
        )

        securityContextTargetPid() shouldBe ON_BEHALF_OF_PID
    }

    test("enrichAuthentication sets target PID from OBO cookie if valid representasjon") {
        arrangeSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            authTypeDeducer = mockk(relaxed = true),
            pidExtractor = arrangeSecurityContextPidExtractor(pid = LOGGED_IN_PID),
            pidDecrypter = mockk(),
            representasjonService = arrange(Representasjon(isValid = true, fullmaktGiverNavn = "F. Giver"))
        ).enrichAuthentication(
            request = arrangeOnBehalfOfCookie(value = ON_BEHALF_OF_PID.pid),
            response = mockk()
        )

        securityContextTargetPid() shouldBe ON_BEHALF_OF_PID
    }

    test("enrichAuthentication throws AccessDeniedException if invalid representasjon") {
        arrangeSecurityContext(authentication = mockk())

        shouldThrow<org.springframework.security.access.AccessDeniedException> {
            SecurityContextEnricher(
                tokenSuppliers,
                authTypeDeducer = mockk(relaxed = true),
                pidExtractor = arrangeSecurityContextPidExtractor(pid = LOGGED_IN_PID),
                pidDecrypter = mockk(),
                representasjonService = arrange(Representasjon(isValid = false, fullmaktGiverNavn = ""))
            ).enrichAuthentication(
                request = arrangeOnBehalfOfCookie(value = ON_BEHALF_OF_PID.pid),
                response = mockk()
            )
        }.message shouldBe "INVALID_REPRESENTASJON"

        securityContextTargetPid() shouldBe LOGGED_IN_PID // not on-behalf-of PID
    }
})

private val LOGGED_IN_PID = Pid("22925399748")
private val ON_BEHALF_OF_PID = Pid("12906498357")

private fun arrangeSecurityContext(authentication: Authentication) {
    SecurityContextHolder.setContext(SecurityContextImpl(authentication))
}

private fun securityContextTargetPid() =
    SecurityContextHolder.getContext().authentication?.enriched()?.targetPid()

private fun arrangeFoedselsnummerInUrl(value: String?) =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { getHeader("pid") } returns null
        every { getParameter("pid") } returns value
    }

private fun arrangeFoedselsnummerInHeader(value: String?) =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { getHeader("pid") } returns value
        every { getParameter("pid") } returns null
    }

private fun arrangeOnBehalfOfCookie(value: String) =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { cookies } returns listOf(Cookie("nav-obo", value)).toTypedArray()
        every { getParameter("pid") } returns null
    }

private fun arrange(representasjon: Representasjon) =
    mockk<RepresentasjonService>().apply {
        every {
            hasValidRepresentasjonsforhold(fullmaktGiverPid = ON_BEHALF_OF_PID)
        } returns representasjon
    }

private fun arrangeDecryption() =
    mockk<PidEncryptionService>().apply {
        every { decryptPid("contains.dot") } returns ON_BEHALF_OF_PID.pid
    }

private fun arrangeSecurityContextPidExtractor(pid: Pid?): SecurityContextPidExtractor =
    mockk<SecurityContextPidExtractor>().apply {
        every { pid() } returns pid
    }
