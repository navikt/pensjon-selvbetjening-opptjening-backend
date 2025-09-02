package no.nav.pensjon.selvbetjeningopptjening.tech.selftest

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService

data class PingResult(
    val service: EgressService,
    val status: ServiceStatus,
    val endpoint: String,
    val message: String
)
