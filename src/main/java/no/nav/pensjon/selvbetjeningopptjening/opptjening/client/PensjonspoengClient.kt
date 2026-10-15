package no.nav.pensjon.selvbetjeningopptjening.opptjening.client

import no.nav.pensjon.selvbetjeningopptjening.health.Pingable
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

interface PensjonspoengClient : Pingable {
    fun getPensjonspoengListe(pid: Pid): List<Pensjonspoeng>
}