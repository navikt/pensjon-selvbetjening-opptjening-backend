package no.nav.pensjon.selvbetjeningopptjening.person

import java.time.LocalDate

data class Person2(
    val navn: String,
    val foedselsdato: LocalDate,
    val sivilstand: Sivilstand = Sivilstand.UOPPGITT,
    val adressebeskyttelse: AdressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT
) {
    val harFoedselsdato = foedselsdato >= minimumFoedselsdato

    private companion object {
        private val minimumFoedselsdato: LocalDate = LocalDate.of(1901, 1, 1)
    }
}
