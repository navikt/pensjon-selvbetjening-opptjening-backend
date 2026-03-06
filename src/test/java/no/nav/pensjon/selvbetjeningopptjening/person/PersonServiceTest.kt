package no.nav.pensjon.selvbetjeningopptjening.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.TestFnrs
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import java.time.LocalDate

class PersonServiceTest : ShouldSpec({

    val pid = Pid(TestFnrs.NORMAL)

    should("use that birthdate when PDL returns one birthdate") {
        val pdlConsumer = mockk<PdlConsumer> {
            every {
                getPerson(any())
            } returns Person(
                pid = pid,
                fornavn = null,
                mellomnavn = null,
                etternavn = null,
                foedselsdato = Foedselsdato2(LocalDate.of(1982, 3, 4))
            )
        }

        PersonService(pdlConsumer).getPerson(pid).getFodselsdato() shouldBe LocalDate.of(1982, 3, 4)
    }
})