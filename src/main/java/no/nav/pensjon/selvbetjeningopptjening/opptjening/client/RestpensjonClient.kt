package no.nav.pensjon.selvbetjeningopptjening.opptjening.client

import no.nav.pensjon.selvbetjeningopptjening.health.Pingable
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon

interface RestpensjonClient : Pingable {
    fun getRestpensjonListe(pid: Pid): List<Restpensjon>
}