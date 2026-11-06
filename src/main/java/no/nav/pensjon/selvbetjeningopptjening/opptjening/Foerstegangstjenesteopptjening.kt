package no.nav.pensjon.selvbetjeningopptjening.opptjening

@ConsistentCopyVisibility
data class Foerstegangstjenesteopptjening private constructor(
    val aar: Int,
    val beloep: Double
) {
    constructor(
        aar: Int?,
        beloep: Double?
    ) : this(
        aar = aar ?: 1900,
        beloep = beloep ?: 0.0
    )
}