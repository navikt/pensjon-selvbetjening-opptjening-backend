package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.fortrolig

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.person.AdressebeskyttelseGradering
import no.nav.pensjon.selvbetjeningopptjening.person.Person2
import no.nav.pensjon.selvbetjeningopptjening.person.Sivilstand
import no.nav.pensjon.selvbetjeningopptjening.person.client.PersonClient
import java.time.LocalDate

class FortroligAdresseServiceTest : ShouldSpec({

    should("return person's adressebeskyttelsesgradering") {
        val personClient = mockk<PersonClient>().apply {
            every {
                fetchAdressebeskyttelse(any())
            } returns Person2(
                navn = "F E",
                foedselsdato = LocalDate.MIN,
                sivilstand = Sivilstand.UOPPGITT,
                adressebeskyttelse = AdressebeskyttelseGradering.STRENGT_FORTROLIG
            )
        }

        FortroligAdresseService(personClient).adressebeskyttelseGradering(pid) shouldBe
                AdressebeskyttelseGradering.STRENGT_FORTROLIG
    }

    should("return 'unknown' by default") {
        val personClient = mockk<PersonClient>().apply {
            every { fetchAdressebeskyttelse(any()) } returns null
        }

        FortroligAdresseService(personClient).adressebeskyttelseGradering(pid) shouldBe
                AdressebeskyttelseGradering.UNKNOWN
    }
})
