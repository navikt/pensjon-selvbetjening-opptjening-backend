package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.fortrolig

import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.person.AdressebeskyttelseGradering
import no.nav.pensjon.selvbetjeningopptjening.person.Person2
import no.nav.pensjon.selvbetjeningopptjening.person.Sivilstand
import no.nav.pensjon.selvbetjeningopptjening.person.client.PersonClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class FortroligAdresseServiceTest {

    @Mock
    private lateinit var personClient: PersonClient

    @Test
    fun `adressebeskyttelseGradering returns person's adressebeskyttelsesgradering`() {
        `when`(personClient.fetchAdressebeskyttelse(pid)).thenReturn(
            Person2(
                navn = "F",
                foedselsdato = LocalDate.MIN,
                sivilstand = Sivilstand.UOPPGITT,
                adressebeskyttelse = AdressebeskyttelseGradering.STRENGT_FORTROLIG
            )
        )

        val gradering = FortroligAdresseService(personClient).adressebeskyttelseGradering(pid)

        assertEquals(AdressebeskyttelseGradering.STRENGT_FORTROLIG, gradering)
    }

    @Test
    fun `adressebeskyttelseGradering returns 'unknown' by default`() {
        `when`(personClient.fetchAdressebeskyttelse(pid)).thenReturn(null)
        val gradering = FortroligAdresseService(personClient).adressebeskyttelseGradering(pid)
        assertEquals(AdressebeskyttelseGradering.UNKNOWN, gradering)
    }
}
