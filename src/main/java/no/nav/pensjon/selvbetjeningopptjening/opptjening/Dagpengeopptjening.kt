package no.nav.pensjon.selvbetjeningopptjening.opptjening

@ConsistentCopyVisibility
data class Dagpengeopptjening private constructor(
    val aar: Int,
    val ordinaertBeloep: Double,
    val fiskerBeloep: Double
) {
    constructor(
        aar: Int?,
        ordinaertBeloep: Double? = null,
        fiskerBeloep: Double? = null
    ) : this(
        aar = aar ?: 1900,
        ordinaertBeloep = ordinaertBeloep ?: 0.0,
        fiskerBeloep = fiskerBeloep ?: 0.0
    )

    fun harPositivtBeloep(): Boolean =
        ordinaertBeloep > 0 || fiskerBeloep > 0
}