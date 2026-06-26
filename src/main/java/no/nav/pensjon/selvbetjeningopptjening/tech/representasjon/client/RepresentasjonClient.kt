package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjonstype

interface RepresentasjonClient {

    fun hasValidRepresentasjonsforhold(fullmaktGiverPid: Pid, representasjonstyper: List<Representasjonstype>): Representasjon
}
