package no.nav.pensjon.selvbetjeningopptjening.opptjening.client

import no.nav.pensjon.selvbetjeningopptjening.health.Pingable
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

interface BeholdningClient : Pingable {
    fun getPensjonsbeholdning(pid: Pid): List<Beholdning>
}