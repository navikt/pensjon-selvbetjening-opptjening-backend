package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad

data class Merknad(
    val kode: Merknadkode,
    val uttaksgrad: Uttaksgrad? = null
)