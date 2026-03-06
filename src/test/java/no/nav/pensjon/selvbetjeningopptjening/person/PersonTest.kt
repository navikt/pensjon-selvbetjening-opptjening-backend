package no.nav.pensjon.selvbetjeningopptjening.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator
import java.time.LocalDate

class PersonTest : ShouldSpec({

    should("use fødselsdato when fødselsdato argument not null") {
        val expectedFoedselsdato = LocalDate.now().minusYears(65)

        Person(
            pid = PidGenerator.generatePidAtAge(65),
            fornavn = null,
            mellomnavn = null,
            etternavn = null,
            foedselsdato = Foedselsdato2(expectedFoedselsdato)
        ).getFodselsdato() shouldBe expectedFoedselsdato
    }

    should("set names on person") {
        val expectedFornavn = "fornavn"
        val expectedMellomnavn = "mellomnavn"
        val expectedEtternavn = "etternavn"

        with(
            Person(
                pid = PidGenerator.generatePidAtAge(65),
                fornavn = expectedFornavn,
                mellomnavn = expectedMellomnavn,
                etternavn = expectedEtternavn,
                foedselsdato = null
            )
        ) {
            fornavn shouldBe expectedFornavn
            mellomnavn shouldBe expectedMellomnavn
            etternavn shouldBe expectedEtternavn
        }
    }
})
