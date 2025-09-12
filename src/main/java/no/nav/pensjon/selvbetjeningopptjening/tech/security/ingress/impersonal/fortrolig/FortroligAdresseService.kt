package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.fortrolig

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.person.AdressebeskyttelseGradering
import no.nav.pensjon.selvbetjeningopptjening.person.client.PersonClient
import org.springframework.stereotype.Service

@Service
class FortroligAdresseService(private val personClient: PersonClient) {

    fun adressebeskyttelseGradering(pid: Pid): AdressebeskyttelseGradering =
        personClient.fetchAdressebeskyttelse(pid)?.adressebeskyttelse ?: AdressebeskyttelseGradering.UNKNOWN
}
