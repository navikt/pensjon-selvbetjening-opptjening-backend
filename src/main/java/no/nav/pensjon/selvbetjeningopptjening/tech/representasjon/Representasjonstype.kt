package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

enum class Representasjonstype {
    PENSJON_LES,
    PENSJON_SKRIV,
    VERGE_PENSJON_LES,
    VERGE_PENSJON_SKRIV;

    companion object {
        val VALID_SKRIV_TYPES = listOf(
            PENSJON_LES,
            PENSJON_SKRIV,
            VERGE_PENSJON_LES,
            VERGE_PENSJON_SKRIV;
        )
    }
}
