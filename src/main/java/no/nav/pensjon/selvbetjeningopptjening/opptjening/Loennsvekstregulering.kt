package no.nav.pensjon.selvbetjeningopptjening.opptjening

import java.time.LocalDate

@ConsistentCopyVisibility
data class Loennsvekstregulering private constructor(
    val beloep: Double,
    val harBeloep: Boolean,
    val reguleringsdato: LocalDate?
) {
    constructor(
        beloep: Double?,
        reguleringsdato: LocalDate? = null
    ) : this(
        beloep = beloep ?: 0.0,
        harBeloep = beloep != null,
        reguleringsdato = reguleringsdato
    )
}