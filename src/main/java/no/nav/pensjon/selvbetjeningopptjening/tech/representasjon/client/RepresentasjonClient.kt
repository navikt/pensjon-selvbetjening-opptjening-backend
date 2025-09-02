package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon

interface RepresentasjonClient {

    fun hasValidRepresentasjonsforhold(fullmaktGiverPid: Pid): Representasjon
}
