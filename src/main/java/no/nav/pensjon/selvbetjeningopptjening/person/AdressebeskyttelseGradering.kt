package no.nav.pensjon.selvbetjeningopptjening.person

enum class AdressebeskyttelseGradering(var fortrolighet: Fortrolighet) {
    UNKNOWN(Fortrolighet.STRENG), // conservatively assuming "streng"
    FORTROLIG(Fortrolighet.MILD), // formerly known as "kode 7"
    STRENGT_FORTROLIG(Fortrolighet.STRENG), // formerly known as "kode 6"
    STRENGT_FORTROLIG_UTLAND(Fortrolighet.STRENG), // Forvaltningsloven paragraph 19
    UGRADERT(Fortrolighet.INGEN)
}

enum class Fortrolighet {
    INGEN,
    MILD,
    STRENG
}
