package no.nav.pensjon.selvbetjeningopptjening.opptjening

@ConsistentCopyVisibility
data class Omsorgsopptjening private constructor(
    val aar: Int,
    val beloep: Double,
    val omsorgListe: List<Omsorg>
) {
    constructor(
        aar: Int?,
        beloep: Double?,
        omsorgListe: List<Omsorg>? = null
    ) : this(
        aar = aar ?: 1900,
        beloep = beloep ?: 0.0,
        omsorgListe = omsorgListe.orEmpty()
    )
}