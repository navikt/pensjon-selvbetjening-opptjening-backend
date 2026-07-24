package no.nav.pensjon.selvbetjeningopptjening.mock

import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Dagpengeopptjening
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Forstegangstjenesteopptjening
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Omsorgsopptjening
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningBasis
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk
import org.springframework.security.oauth2.jwt.Jwt
import java.time.LocalDate

object TestObjects {
    val jwt = jwt(claims = mapOf("k" to "v"))

    val pid = Pid("22925399748")

    val emptyOpptjeningBasis =
        OpptjeningBasis(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            AfpHistorikk(LocalDate.of(2021, 1, 1), null),
            UforeHistorikk(emptyList())
        )

    fun beholdning(
        aar: Int,
        ordinaerDagpengeopptjening: Double? = null,
        fiskerDagpengeopptjening: Double? = null,
        foerstegangstjenesteopptjening: Double? = null,
        omsorgsopptjening: Double? = null
    ) =
        Beholdning(
            1,
            pid.pid,
            "S",
            "T",
            1.2,
            2,
            LocalDate.of(aar, 1, 1),
            LocalDate.of(aar, 12, 31),
            2.3,
            2.2,
            3.4,
            3.3,
            "Å",
            null,
            null,
            omsorgsopptjening?.let { Omsorgsopptjening(aar, it, null) },
            ordinaerDagpengeopptjening?.let { Dagpengeopptjening(aar, it, null) }
                ?: fiskerDagpengeopptjening?.let { Dagpengeopptjening(aar, null, it) },
            foerstegangstjenesteopptjening?.let { Forstegangstjenesteopptjening(aar, it) },
            null
        )

    fun jwt(claims: Map<String, Any>) =
        Jwt(
            "j.w.t",
            null,
            null,
            mapOf("k" to "v"),
            claims
        )
}