package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode

data class Merknader(
    val perAar: Map<Int, List<MerknadCode>>
)