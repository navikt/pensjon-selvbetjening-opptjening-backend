package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

/**
 * Represents a 'raw' JSON Web Token (JWT), i.e., the token as a simple string.
 */
data class RawJwt(val value: String)
