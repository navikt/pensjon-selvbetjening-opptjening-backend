package no.nav.pensjon.selvbetjeningopptjening.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator.generatePid
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.emptyOpptjeningBasis
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import no.nav.pensjon.selvbetjeningopptjening.testutil.Arrange
import java.time.LocalDate

class OpptjeningAssemblerForUserGroup4Test : ShouldSpec({

    should("øke andel pensjon basert på beholdning med 1 for hvert år") {
        val expectedPerAar: Map<Int, Int> =
            mapOf(
                1954 to 1,
                1955 to 2,
                1956 to 3,
                1957 to 4,
                1958 to 5,
                1959 to 6,
                1960 to 7,
                1961 to 8,
                1962 to 9
            )
        Arrange.security()

        expectedPerAar.keys.forEach {
            val foedselsdato = LocalDate.of(it, 5, 5)

            OpptjeningAssemblerForUserGroup4(mockk()).createResponse(
                Person(
                    pid = generatePid(foedselsdato),
                    fornavn = null,
                    mellomnavn = null,
                    etternavn = null,
                    foedselsdato = Foedselsdato2(foedselsdato)
                ),
                emptyOpptjeningBasis
            ).andelPensjonBasertPaBeholdning shouldBe expectedPerAar[it]
        }
    }
})
