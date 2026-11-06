package no.nav.pensjon.selvbetjeningopptjening.opptjening

@ConsistentCopyVisibility
data class Inntektsopptjening private constructor(
    val aar: Int,
    val beloep: Double,
    val sumPensjonsgivendeInntekt: Inntekt?
) {
    constructor(
        aar: Int? = null,
        beloep: Double? = null,
        sumPensjonsgivendeInntekt: Inntekt? = null
    ) : this(
        aar = aar ?: 1900,
        beloep = beloep ?: 0.0,
        sumPensjonsgivendeInntekt = sumPensjonsgivendeInntekt
    )
}