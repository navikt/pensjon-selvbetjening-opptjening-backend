package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.client.SkjermingClient

class SkjermingServiceTest : ShouldSpec({

    should("return 'false' when person er skjermet") {
        val client = arrangeClient(erTilgjengelig = false)
        SkjermingService(client).personErTilgjengelig(pid) shouldBe false
    }

    should("return 'true' when person ikke er skjermet") {
        val client = arrangeClient(erTilgjengelig = true)
        SkjermingService(client).personErTilgjengelig(pid) shouldBe true
    }
})

private fun arrangeClient(erTilgjengelig: Boolean): SkjermingClient =
    mockk<SkjermingClient>().apply {
        every { personErTilgjengelig(any()) } returns erTilgjengelig
    }
