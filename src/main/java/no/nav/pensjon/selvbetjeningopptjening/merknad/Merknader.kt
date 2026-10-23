package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad

data class Merknader(
    val perAar: Map<Int, List<Merknad>>
)

data class Merknad(
    val kode: MerknadCode,
    val uttaksgrad: Uttaksgrad? = null
)