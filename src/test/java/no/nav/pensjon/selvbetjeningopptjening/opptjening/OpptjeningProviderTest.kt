package no.nav.pensjon.selvbetjeningopptjening.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator.generatePid
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import no.nav.pensjon.selvbetjeningopptjening.person.PersonService
import no.nav.pensjon.selvbetjeningopptjening.testutil.Arrange
import java.time.LocalDate

class OpptjeningProviderTest : ShouldSpec({

    should("set fødselsår on response when user group 1/2/3") {
        val pid = generatePid(userGroup123Foedselsdato)
        Arrange.security()
        val opptjeningResponse = OpptjeningProvider(
            mockk(),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(),
            mockk(relaxed = true),
            arrangePerson(pid, userGroup123Foedselsdato),
            mockk(relaxed = true),
        ).calculateOpptjeningForFnr(pid)

        opptjeningResponse.fodselsaar shouldBe USER_GROUP_1_2_3_FOEDSELSAAR
    }

    should("set fødselsår on response when user group 4") {
        val pid = generatePid(userGroup4Foedselsdato)
        Arrange.security()
        val opptjeningResponse = OpptjeningProvider(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(),
            mockk(relaxed = true),
            arrangePerson(pid, userGroup4Foedselsdato),
            mockk(relaxed = true),
        ).calculateOpptjeningForFnr(pid)

        opptjeningResponse.fodselsaar shouldBe USER_GROUP_4_FOEDSELSAAR
    }

    should("set fødselsår on response when user group 5") {
        val pid = generatePid(userGroup5Foedselsdato)
        Arrange.security()
        val opptjeningResponse = OpptjeningProvider(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(),
            mockk(),
            mockk(relaxed = true),
            arrangePerson(pid, userGroup5Foedselsdato),
            mockk(relaxed = true),
        ).calculateOpptjeningForFnr(pid)

        opptjeningResponse.fodselsaar shouldBe USER_GROUP_5_FOEDSELSAAR
    }
})

private val USER_GROUP_1_2_3_FOEDSELSAAR = 1950
private val USER_GROUP_4_FOEDSELSAAR = 1956
private val USER_GROUP_5_FOEDSELSAAR = 1968
private val userGroup123Foedselsdato = LocalDate.of(USER_GROUP_1_2_3_FOEDSELSAAR, 7, 6)
private val userGroup5Foedselsdato = LocalDate.of(USER_GROUP_5_FOEDSELSAAR, 7, 6)
private val userGroup4Foedselsdato = LocalDate.of(USER_GROUP_4_FOEDSELSAAR, 7, 6)

private fun arrangePerson(pid: Pid, foedselsdato: LocalDate): PersonService =
    mockk<PersonService>().apply {
        every {
            getPerson(any())
        } returns Person(
            pid,
            fornavn = null,
            mellomnavn = null,
            etternavn = null,
            foedselsdato = Foedselsdato2(foedselsdato)
        )
    }
