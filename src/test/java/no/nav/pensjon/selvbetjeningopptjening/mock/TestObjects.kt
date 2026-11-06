package no.nav.pensjon.selvbetjeningopptjening.mock

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
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
            id = 1,
            pid = pid.pid,
            status = "S",
            type = "T",
            beloep = 1.2,
            vedtakId = 2,
            fom = LocalDate.of(aar, 1, 1),
            tom = LocalDate.of(aar, 12, 31),
            grunnlag = 2.3,
            avkortetGrunnlag = 2.2,
            innskudd = 3.4,
            innskuddUtenOmsorg = 3.3,
            oppdateringAarsak = "Å",
            loennsvekstregulering = null,
            inntektsopptjening = null,
            omsorgsopptjening = omsorgsopptjening?.let { Omsorgsopptjening(aar, beloep = it) },
            dagpengeopptjening = ordinaerDagpengeopptjening?.let { Dagpengeopptjening(aar, ordinaertBeloep = it) }
                ?: fiskerDagpengeopptjening?.let { Dagpengeopptjening(aar, fiskerBeloep = it) },
            foerstegangstjenesteopptjening = foerstegangstjenesteopptjening?.let {
                Foerstegangstjenesteopptjening(aar, beloep = it)
            },
            ufoereopptjening = null
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