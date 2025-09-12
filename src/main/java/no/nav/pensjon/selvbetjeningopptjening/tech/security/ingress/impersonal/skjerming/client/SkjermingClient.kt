package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.client

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

interface SkjermingClient {
    fun personErTilgjengelig(pid: Pid): Boolean
}
