package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.validation

import java.time.LocalDateTime

fun interface TimeProvider {
    fun time(): LocalDateTime
}
