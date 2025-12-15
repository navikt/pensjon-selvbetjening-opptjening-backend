package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EnrichedAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class TargetPidExtractorTest : ShouldSpec({

    should("throw informative exception if no PID in security context") {
        SecurityContextHolder.setContext(
            SecurityContextImpl(
                mockk<EnrichedAuthentication>().apply { every { targetPid() } returns null })
        )

        shouldThrow<PidValidationException> { TargetPidExtractor().pid() }.message shouldBe
                "Pid validation failed: No PID found"
    }
})
