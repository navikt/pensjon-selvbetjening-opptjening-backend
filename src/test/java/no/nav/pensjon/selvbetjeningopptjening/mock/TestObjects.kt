package no.nav.pensjon.selvbetjeningopptjening.mock

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import org.springframework.security.oauth2.jwt.Jwt
import kotlin.to

object TestObjects {
    val jwt = Jwt("j.w.t", null, null, mapOf("k" to "v"), mapOf("k" to "v"))

    val pid = Pid("22925399748")
}
