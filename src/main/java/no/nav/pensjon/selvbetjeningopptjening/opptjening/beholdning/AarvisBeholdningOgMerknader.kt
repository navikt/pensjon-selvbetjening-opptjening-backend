package no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning

data class AarvisBeholdningOgMerknader(
    val perAar: Map<Int, BeholdningOgMerknader>
)