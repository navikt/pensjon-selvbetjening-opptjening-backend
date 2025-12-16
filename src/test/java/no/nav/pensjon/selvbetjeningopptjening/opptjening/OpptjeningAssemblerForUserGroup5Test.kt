package no.nav.pensjon.selvbetjeningopptjening.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.emptyOpptjeningBasis
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import no.nav.pensjon.selvbetjeningopptjening.testutil.Arrange

class OpptjeningAssemblerForUserGroup5Test : ShouldSpec({

    should("returnere andel pensjon basert på beholdning = 10") {
        Arrange.security()

        OpptjeningAssemblerForUserGroup5(mockk()).createResponse(
            Person(
                PidGenerator.generatePidAtAge(50),
                null,
                null,
                null,
                null
            ),
            emptyOpptjeningBasis
        ).andelPensjonBasertPaBeholdning shouldBe 10
    }
})
