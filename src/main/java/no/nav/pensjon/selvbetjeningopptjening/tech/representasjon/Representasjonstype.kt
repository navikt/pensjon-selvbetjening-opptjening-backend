package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

enum class Representasjonstype {
    PENSJON_FULLSTENDIG,
    PENSJON_BEGRENSET,
    PENSJON_SKRIV,
    PENSJON_KOMMUNISER,
    PENSJON_LES,
    PENSJON_PENGEMOTTAKER,
    PENSJON_VERGE,
    PENSJON_VERGE_PENGEMOTTAKER;

    companion object {
        val VALID_SKRIV_TYPES = listOf(
            PENSJON_FULLSTENDIG,
            PENSJON_BEGRENSET,
            PENSJON_SKRIV,
            PENSJON_KOMMUNISER
        )
    }
}
