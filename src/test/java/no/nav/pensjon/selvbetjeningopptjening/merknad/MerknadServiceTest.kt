package no.nav.pensjon.selvbetjeningopptjening.merknad

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.beholdning
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import no.nav.pensjon.selvbetjeningopptjening.person.PersonService
import java.time.LocalDate

class MerknadServiceTest : ShouldSpec({

    context("opptjening mulig f.o.m. 2024, har AFP i 2025") {
        should("gi merknadene 'ingen opptjening' for 2024, 'AFP' for 2025") {
            MerknadService(
                personService = arrangePerson(), // født 2011 => opptjening mulig f.o.m. 2024
                personConsumer = arrangeAfp(), // har AFP i 2025
                uttaksgradGetter = mockk(relaxed = true),
                opptjeningsgrunnlagConsumer = arrangeInntekt(),
                pensjonsbeholdningConsumer = arrangeBeholdning(), // har beholdning (dvs. opptjening) i 2025
                pensjonspoengConsumer = mockk(),
                restpensjonConsumer = mockk()
            ).merknaderPerAar(pid) shouldBe
                    Merknader(
                        perAar = mapOf(
                            2024 to listOf(MerknadCode.INGEN_OPPTJENING),
                            2025 to listOf(MerknadCode.AFP)
                        )
                    )
        }
    }
})

/**
 * AFP i 2025
 */
private fun arrangeAfp(): PersonConsumer =
    mockk(relaxed = true) {
        every {
            getAfpHistorikkForPerson(any())
        } returns AfpHistorikk(
            virkningFomDate = LocalDate.of(2025, 1, 1),
            virkningTomDate = LocalDate.of(2030, 12, 31)
        )
    }

/**
 * Beholdning (dvs. opptjening) i 2025
 */
private fun arrangeBeholdning(): PensjonsbeholdningConsumer =
    mockk {
        every {
            getPensjonsbeholdning(any())
        } returns listOf(beholdning(aar = 2025))
    }

private fun arrangeInntekt(): OpptjeningsgrunnlagConsumer =
    mockk {
        every {
            getInntektListeFromOpptjeningsgrunnlag(any(), any(), any())
        } returns listOf(Inntekt(2025, "T", 111000L))
    }

private fun arrangePerson(): PersonService =
    mockk {
        every {
            getPerson(any())
        } returns Person(
            pid = pid,
            fornavn = "F",
            mellomnavn = "M",
            etternavn = "E",
            foedselsdato = Foedselsdato2(
                value = LocalDate.of(2011, 3, 4),
                basedOnYearOnly = false
            )
        )
    }
