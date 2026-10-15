package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import java.time.LocalDate

data class PoppBeholdning(
    val beholdningId: Long? = null,
    val fnr: String? = null,
    val status: String? = null,
    val beholdningType: String? = null,
    val belop: Double? = null,
    val vedtakId: Long? = null,
    val fomDato: LocalDate? = null,
    val tomDato: LocalDate? = null,
    val beholdningGrunnlag: Double? = null,
    val beholdningGrunnlagAvkortet: Double? = null,
    val beholdningInnskudd: Double? = null,
    val beholdningInnskuddUtenOmsorg: Double? = null,
    val oppdateringArsak: String? = null,
    val lonnsvekstregulering: PoppLoennsvekstregulering? = null,
    val inntektOpptjeningBelop: PoppInntektOpptjeningBeloep? = null,
    val omsorgOpptjeningBelop: PoppOmsorgOpptjeningBeloep? = null,
    val dagpengerOpptjeningBelop: PoppDagpengerOpptjeningBeloep? = null,
    val forstegangstjenesteOpptjeningBelop: PoppFoerstegangstjenesteOpptjeningBeloep? = null,
    val uforeOpptjeningBelop: PoppUfoereOpptjeningBeloep? = null
) {
    fun fromDto() =
        Beholdning(
            beholdningId,
            fnr,
            status,
            beholdningType,
            belop,
            vedtakId,
            fomDato,
            tomDato,
            beholdningGrunnlag,
            beholdningGrunnlagAvkortet,
            beholdningInnskudd,
            beholdningInnskuddUtenOmsorg,
            oppdateringArsak,
            lonnsvekstregulering?.fromDto(),
            inntektOpptjeningBelop?.fromDto(),
            omsorgOpptjeningBelop?.fromDto(),
            dagpengerOpptjeningBelop?.fromDto(),
            forstegangstjenesteOpptjeningBelop?.fromDto(),
            uforeOpptjeningBelop?.fromDto()
        )
}