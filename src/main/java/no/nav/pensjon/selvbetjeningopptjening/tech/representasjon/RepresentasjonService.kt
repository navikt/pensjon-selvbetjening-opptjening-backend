package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.RepresentasjonClient
import org.springframework.stereotype.Component

@Component
class RepresentasjonService(private val client: RepresentasjonClient) {

    fun hasValidRepresentasjonsforhold(representertPid: String, representasjonstyper: List<Representasjonstype>): Representasjon =
        client.hasValidRepresentasjonsforhold(representertPid, representasjonstyper)
}
