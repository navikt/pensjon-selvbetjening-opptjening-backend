package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.acl

import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.acl.PoppPensjonspoeng
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon
import java.time.LocalDate

/**
 * Korresponderer med RestpensjonDto i POPP
 */
data class PoppRestpensjon(
    val fnr: String? = null,
    val restGrunnpensjon: Double? = null,
    val restTilleggspensjon: Double? = null,
    val restPensjonstillegg: Double? = null,
    val vedtakId: Long? = null,
    val fomDato: LocalDate? = null,
    val tomDato: LocalDate? = null,
    val pensjonspoengListe: List<PoppPensjonspoeng>? = null,
    val oppdateringArsak: String? = null
) {
    fun fromDto() =
        Restpensjon(
            fomDato,
            restGrunnpensjon,
            restTilleggspensjon,
            restPensjonstillegg
        )
}