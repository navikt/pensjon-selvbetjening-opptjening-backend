package no.nav.pensjon.selvbetjeningopptjening.opptjening

@ConsistentCopyVisibility
data class Ufoereopptjening private constructor(
    val ufoeregrad: Int,
    val beloep: Double
) {
    constructor(
        ufoeregrad: Int?,
        beloep: Double?
    ) : this(
        ufoeregrad = ufoeregrad ?: 0,
        beloep = beloep ?: 0.0
    )
}