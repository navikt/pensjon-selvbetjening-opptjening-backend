package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

import java.time.LocalDateTime

data class TokenData(
    val accessToken: String,
    val idToken: String,
    val refreshToken: String,
    val issuedTime: LocalDateTime,
    val expiresInSeconds: Long
)
