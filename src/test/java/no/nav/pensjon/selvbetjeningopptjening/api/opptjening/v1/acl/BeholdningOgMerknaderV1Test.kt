package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknader
import java.time.LocalDate
import java.time.Month

class BeholdningOgMerknaderV1Test : ShouldSpec({

    context("funksjonen 'transferable'") {
        should("konvertere fra intern domenerepresentasjon til overførbart format") {
            BeholdningOgMerknaderV1.transferable(
                BeholdningOgMerknader(
                    beholdning = beholdning(beloep = 222000),
                    merknadListe = listOf(
                        Merknad(
                            kode = MerknadCode.GRADERT_UTTAK,
                            uttaksgrad = Uttaksgrad(
                                grad = 80,
                                fom = LocalDate.of(2021, Month.JULY, 1)
                            )
                        )
                    )
                ),
                aar = 2021
            ) shouldBe BeholdningOgMerknaderV1(
                beholdning = BeholdningV1(beloep = 222000),
                merknadListe = listOf(
                    MerknadV1(
                        kode = MerknadkodeV1.GRADERT_UTTAK,
                        uttaksgrad = UttaksgradV1(
                            grad = 80,
                            kalendermaanedFom = 7
                        )
                    )
                )
            )
        }

        should("utelate merknader av type 'ingen opptjening'") {
            BeholdningOgMerknaderV1.transferable(
                BeholdningOgMerknader(
                    beholdning = beholdning(beloep = 111000),
                    merknadListe = listOf(Merknad(kode = MerknadCode.INGEN_OPPTJENING))
                ),
                aar = 2021
            ) shouldBe BeholdningOgMerknaderV1(
                beholdning = BeholdningV1(beloep = 111000),
                merknadListe = emptyList()
            )
        }
    }
})

private fun beholdning(beloep: Int) =
    Beholdning(
        null,
        null,
        null,
        null,
        beloep.toDouble(),
        null,
        LocalDate.of(2021, Month.JANUARY, 1),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )