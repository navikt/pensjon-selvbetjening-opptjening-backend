package no.nav.pensjon.selvbetjeningopptjening.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter

class OpptjeningAssemblerTest : ShouldSpec({

    should("remove entry for given year") {
        val opptjeningerByYear: Map<Int, Opptjening> = mapOf(
            2018 to opptjening(),
            2019 to opptjening(),
            2020 to opptjening()
        )

        TestOpptjeningAssembler(
            getter = mockk<UttaksgradGetter>()
        ).removeFutureOpptjening(opptjeningerByYear, 2019)

        opptjeningerByYear shouldHaveSize 2
    }
})

private class TestOpptjeningAssembler(getter: UttaksgradGetter) :
    OpptjeningAssembler(getter)

private fun opptjening() =
    Opptjening(1L, 1.1)