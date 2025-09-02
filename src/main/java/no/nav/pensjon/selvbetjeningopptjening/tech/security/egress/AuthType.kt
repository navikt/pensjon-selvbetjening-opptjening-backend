package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

enum class AuthType {
    NONE,
    PERSON_SELF,
    PERSON_ON_BEHALF,
    MACHINE_INSIDE_NAV,
    MACHINE_OUTSIDE_NAV
}
