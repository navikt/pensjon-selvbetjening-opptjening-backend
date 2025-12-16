package no.nav.pensjon.selvbetjeningopptjening.mock

import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningBasis
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk
import org.springframework.security.oauth2.jwt.Jwt
import java.time.LocalDate
import kotlin.to

object TestObjects {
    val jwt = Jwt("j.w.t", null, null, mapOf("k" to "v"), mapOf("k" to "v"))

    val pid = Pid("22925399748")

    val emptyOpptjeningBasis =
        OpptjeningBasis(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            AfpHistorikk(LocalDate.of(2021, 1, 1), null),
            UforeHistorikk(emptyList())
        )
}
