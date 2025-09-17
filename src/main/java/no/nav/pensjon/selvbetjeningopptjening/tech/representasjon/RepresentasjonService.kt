package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.RepresentasjonClient
import org.springframework.stereotype.Component

@Component
class RepresentasjonService(private val client: RepresentasjonClient) {

    fun hasValidRepresentasjonsforhold(fullmaktGiverPid: Pid): Representasjon =
        client.hasValidRepresentasjonsforhold(fullmaktGiverPid)
}
