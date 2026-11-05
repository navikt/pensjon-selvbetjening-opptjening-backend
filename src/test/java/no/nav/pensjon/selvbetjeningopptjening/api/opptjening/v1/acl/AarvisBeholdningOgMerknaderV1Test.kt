package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.AarvisBeholdningOgMerknader
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknader
import java.time.LocalDate
import java.time.Month

class AarvisBeholdningOgMerknaderV1Test : ShouldSpec({

    should("map from domain representation to transferable representation") {
        AarvisBeholdningOgMerknaderV1.transferable(
            AarvisBeholdningOgMerknader(
                perAar = mapOf(
                    2001 to BeholdningOgMerknader(
                        beholdning(aar = 2001, beloep = 210000),
                        merknadListe = listOf(
                            Merknad(
                                kode = MerknadCode.GRADERT_UTTAK,
                                uttaksgrad = Uttaksgrad(
                                    grad = 40,
                                    fom = LocalDate.of(2001, 7, 1)
                                )
                            ),
                        )
                    ),
                    2002 to BeholdningOgMerknader(
                        beholdning(aar = 2002, beloep = 220000),
                        merknadListe = listOf(Merknad(kode = MerknadCode.AFP), Merknad(kode = MerknadCode.DAGPENGER))
                    )
                )
            )
        ) shouldBe
                AarvisBeholdningOgMerknaderV1(
                    perAar = mapOf(
                        2001 to BeholdningOgMerknaderV1(
                            BeholdningV1(beloep = 210000),
                            merknadListe = listOf(
                                MerknadV1(
                                    kode = MerknadkodeV1.GRADERT_UTTAK,
                                    uttaksgrad = UttaksgradV1(
                                        grad = 40,
                                        kalendermaanedFom = 7
                                    )
                                )
                            )
                        ),
                        2002 to BeholdningOgMerknaderV1(
                            BeholdningV1(beloep = 220000),
                            merknadListe = listOf(
                                MerknadV1(kode = MerknadkodeV1.AFP),
                                MerknadV1(kode = MerknadkodeV1.DAGPENGER)
                            )
                        )
                    )
                )
    }
})

private fun beholdning(aar: Int, beloep: Int) =
    Beholdning(
        null,
        null,
        null,
        null,
        beloep.toDouble(),
        null,
        LocalDate.of(aar, Month.JANUARY, 1),
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