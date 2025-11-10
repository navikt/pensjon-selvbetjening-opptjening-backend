package no.nav.pensjon.selvbetjeningopptjening.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator
import java.time.LocalDate

class PersonTest : ShouldSpec({

    should("use fødselsdato when fødselsdato argument not null") {
        val expectedFoedselsdato = LocalDate.now().minusYears(65)

        val person = Person(
            pid = PidGenerator.generatePidAtAge(65),
            fornavn = null,
            mellomnavn = null,
            etternavn = null,
            foedselsdato = Foedselsdato2(expectedFoedselsdato)
        )

        person.getFodselsdato() shouldBe expectedFoedselsdato
    }

    should("use default fødselsdato from PID when fødselsdato argument is null") {
        val expectedFoedselsdato = LocalDate.now().minusYears(65)

        val person = Person(
            pid = PidGenerator.generatePid(expectedFoedselsdato),
            fornavn = null,
            mellomnavn = null,
            etternavn = null,
            foedselsdato = null
        )

        person.getFodselsdato() shouldBe expectedFoedselsdato
    }

    should("set names on person") {
        val expectedFornavn = "fornavn"
        val expectedMellomnavn = "mellomnavn"
        val expectedEtternavn = "etternavn"

        val person = Person(
            pid = PidGenerator.generatePidAtAge(65),
            fornavn = expectedFornavn,
            mellomnavn = expectedMellomnavn,
            etternavn = expectedEtternavn,
            foedselsdato = null
        )

        person.fornavn shouldBe expectedFornavn
        person.mellomnavn shouldBe expectedMellomnavn
        person.etternavn shouldBe expectedEtternavn
    }
})
