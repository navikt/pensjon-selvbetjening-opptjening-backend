package no.nav.pensjon.selvbetjeningopptjening.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator.generatePidAtAge
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.emptyOpptjeningBasis
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import no.nav.pensjon.selvbetjeningopptjening.testutil.Arrange
import java.time.LocalDate

class OpptjeningAssemblerForUserGroup5Test : ShouldSpec({

    should("returnere andel pensjon basert på beholdning = 10") {
        Arrange.security()

        OpptjeningAssemblerForUserGroup5(mockk()).createResponse(
            Person(
                pid = generatePidAtAge(50),
                fornavn = null,
                mellomnavn = null,
                etternavn = null,
                foedselsdato = Foedselsdato2(LocalDate.of(1982, 3, 4))
            ),
            emptyOpptjeningBasis
        ).andelPensjonBasertPaBeholdning shouldBe 10
    }
})
