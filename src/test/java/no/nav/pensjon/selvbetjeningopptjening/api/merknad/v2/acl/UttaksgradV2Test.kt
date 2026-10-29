package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v2.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad
import java.time.LocalDate

class UttaksgradV2Test : ShouldSpec({

    context("funksjonen 'transferable'") {
        context("uttak starter i opptjeningsåret") {
            should("angi kalendermaaned (fra og med) da uttaket startet") {
                UttaksgradV2.transferable(
                    source = Uttaksgrad(grad = 20, fom = LocalDate.of(2021, 4, 1)),
                    aar = 2021
                ) shouldBe UttaksgradV2(grad = 20, kalendermaanedFom = 4)
            }
        }

        context("pågående uttak startet før opptjeningsåret") {
            should("utelate kalendermaaned for uttaksgraden") {
                UttaksgradV2.transferable(
                    source = Uttaksgrad(grad = 20, fom = LocalDate.of(2020, 4, 1)),
                    aar = 2021
                ) shouldBe UttaksgradV2(grad = 20, kalendermaanedFom = null)
            }
        }
    }
})