package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

/**
 * Kalkulatoren viser data for en gitt person, identifisert med PID (person-ID).
 * Denne enum brukes for å holde styr på personens rolle i forskjellige representasjonsforhold.
 */
enum class RepresentertRolle(val needFulltNavn: Boolean) {
    NONE(needFulltNavn = false),
    SELV(needFulltNavn = false), // personen er den samme som den som er innlogget
    FULLMAKT_GIVER(needFulltNavn = true), // personen er fullmaktsgiver; den innloggede er fullmektig
    UNDER_VEILEDNING(needFulltNavn = true) // personen undergår veiledning; den innloggede er veileder
}
