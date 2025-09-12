package no.nav.pensjon.selvbetjeningopptjening.person.client

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.person.Person2

interface PersonClient {
    fun fetchPerson(pid: Pid, fetchFulltNavn: Boolean): Person2?

    fun fetchAdressebeskyttelse(pid: Pid): Person2?
}
