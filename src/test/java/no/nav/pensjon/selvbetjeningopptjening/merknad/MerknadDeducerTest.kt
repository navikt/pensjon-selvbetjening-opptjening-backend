package no.nav.pensjon.selvbetjeningopptjening.merknad

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.beholdning
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import java.time.LocalDate

class MerknadDeducerTest : ShouldSpec({

    context("ingen opptjening") {
        should("gi merknad 'ingen opptjening'") {
            MerknadDeducer.merknaderPerAar(
                opptjeningPerAar = ingenOpptjening(aar = 2026),
                beholdningListe = emptyList(),
                afpHistorikk = null,
                ufoereHistorikk = null,
                erBrukergruppe4Eller5 = false
            ) shouldBe mapOf(2026 to listOf(MerknadCode.INGEN_OPPTJENING))
        }

        context("reformår, brukergruppe 4 eller 5") {
            should("utelate merknad 'ingen opptjening'") {
                MerknadDeducer.merknaderPerAar(
                    opptjeningPerAar = ingenOpptjening(aar = 2010), // reformår
                    beholdningListe = emptyList(),
                    afpHistorikk = null,
                    ufoereHistorikk = null,
                    erBrukergruppe4Eller5 = true
                ) shouldBe mapOf(2010 to listOf(MerknadCode.REFORM))
            }
        }
    }

    context("har AFP i opptjeningsperioden") {
        should("gi merknad 'AFP'") {
            MerknadDeducer.merknaderPerAar(
                opptjeningPerAar = opptjening(aar = 2026),
                beholdningListe = emptyList(),
                afpHistorikk = AfpHistorikk(
                    virkningFomDate = LocalDate.of(2026, 1, 1),
                    virkningTomDate = LocalDate.of(2030, 12, 31)
                ),
                ufoereHistorikk = null,
                erBrukergruppe4Eller5 = false
            ) shouldBe mapOf(2026 to listOf(MerknadCode.AFP))
        }
    }

    context("har uføregrad i opptjeningsperioden") {
        should("gi merknad 'uføregrad'") {
            MerknadDeducer.merknaderPerAar(
                opptjeningPerAar = opptjening(aar = 2026),
                beholdningListe = emptyList(),
                afpHistorikk = null,
                ufoereHistorikk = UforeHistorikk(
                    listOf(
                        Uforeperiode(
                            1,
                            UforeTypeCode.UFORE,
                            LocalDate.of(2026, 1, 1),
                            LocalDate.of(2030, 12, 31)
                        )
                    )
                ),
                erBrukergruppe4Eller5 = false
            ) shouldBe mapOf(2026 to listOf(MerknadCode.UFOREGRAD))
        }
    }

    context("brukergruppe 1, 2 eller 3") {
        should("ikke gi merknad 'reform' for 2010") {
            MerknadDeducer.merknaderPerAar(
                opptjeningPerAar = opptjening(aar = 2010),
                beholdningListe = emptyList(),
                afpHistorikk = null,
                ufoereHistorikk = null,
                erBrukergruppe4Eller5 = false
            ) shouldBe mapOf(2010 to emptyList())
        }
    }

    context("brukergruppe 4 eller 5") {
        should("gi merknad 'reform' for 2010") {
            MerknadDeducer.merknaderPerAar(
                opptjeningPerAar = opptjening(aar = 2010),
                beholdningListe = emptyList(),
                afpHistorikk = null,
                ufoereHistorikk = null,
                erBrukergruppe4Eller5 = true
            ) shouldBe mapOf(2010 to listOf(MerknadCode.REFORM))
        }

        context("har omsorgsopptjening i opptjeningsperioden") {
            should("gi merknad 'omsorgsopptjening'") {
                MerknadDeducer.merknaderPerAar(
                    opptjeningPerAar = opptjening(aar = 2026),
                    beholdningListe = listOf(beholdning(aar = 2026, omsorgsopptjening = 123.4)),
                    afpHistorikk = null,
                    ufoereHistorikk = null,
                    erBrukergruppe4Eller5 = true
                ) shouldBe mapOf(2026 to listOf(MerknadCode.OMSORGSOPPTJENING))
            }
        }

        context("har ordinære dagpenger i opptjeningsperioden") {
            should("gi merknad 'dagpenger'") {
                MerknadDeducer.merknaderPerAar(
                    opptjeningPerAar = opptjening(aar = 2026),
                    beholdningListe = listOf(
                        beholdning(aar = 2026, ordinaerDagpengeopptjening = 123.4)
                    ),
                    afpHistorikk = null,
                    ufoereHistorikk = null,
                    erBrukergruppe4Eller5 = true
                ) shouldBe mapOf(2026 to listOf(MerknadCode.DAGPENGER))
            }
        }

        context("har dagpenger som fisker i opptjeningsperioden") {
            should("gi merknad 'dagpenger'") {
                MerknadDeducer.merknaderPerAar(
                    opptjeningPerAar = opptjening(aar = 2026),
                    beholdningListe = listOf(
                        beholdning(aar = 2026, fiskerDagpengeopptjening = 123.4)
                    ),
                    afpHistorikk = null,
                    ufoereHistorikk = null,
                    erBrukergruppe4Eller5 = true
                ) shouldBe mapOf(2026 to listOf(MerknadCode.DAGPENGER))
            }
        }

        context("har førstegangstjeneste i opptjeningsperioden") {
            should("gi merknad 'førstegangstjeneste'") {
                MerknadDeducer.merknaderPerAar(
                    opptjeningPerAar = opptjening(aar = 2025),
                    beholdningListe = listOf(
                        beholdning(aar = 2025, foerstegangstjenesteopptjening = 123.4)
                    ),
                    afpHistorikk = null,
                    ufoereHistorikk = null,
                    erBrukergruppe4Eller5 = true
                ) shouldBe mapOf(2025 to listOf(MerknadCode.FORSTEGANGSTJENESTE))
            }
        }
    }
})

private fun opptjening(aar: Int): Map<Int, Opptjening> =
    mapOf(aar to Opptjening(111000L, 2.3))

private fun ingenOpptjening(aar: Int): Map<Int, Opptjening> =
    mapOf(aar to Opptjening(0, 0.0))