package no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning

data class BeholdningOgMerknader(
    val beholdning: Beholdning,
    val merknadListe: List<Merknad>
)