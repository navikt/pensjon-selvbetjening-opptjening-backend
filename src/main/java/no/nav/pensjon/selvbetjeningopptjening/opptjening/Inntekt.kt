package no.nav.pensjon.selvbetjeningopptjening.opptjening

@ConsistentCopyVisibility
data class Inntekt private constructor(
    val aar: Int,
    val type: String?,
    val beloep: Long
) {
    constructor(
        aar: Int?,
        type: String? = null,
        beloep: Long? = null
    ) : this(
        aar = aar ?: 1900,
        type = type,
        beloep = beloep ?: 0L
    )
}