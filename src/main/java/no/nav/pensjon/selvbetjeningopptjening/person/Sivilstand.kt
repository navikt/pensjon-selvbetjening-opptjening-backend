package no.nav.pensjon.selvbetjeningopptjening.person

enum class Sivilstand(val harEps: Boolean) {
    UNKNOWN(false),
    UOPPGITT(false),
    UGIFT(false),
    GIFT(true),
    ENKE_ELLER_ENKEMANN(false),
    SKILT(false),
    SEPARERT(false),
    REGISTRERT_PARTNER(true),
    SEPARERT_PARTNER(false),
    SKILT_PARTNER(false),
    GJENLEVENDE_PARTNER(false),
    SAMBOER(true)
}
