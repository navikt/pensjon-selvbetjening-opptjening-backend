package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

data class CacheKey(
    val scope: String,
    val pid: Pid
)
