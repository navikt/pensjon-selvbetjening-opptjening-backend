package no.nav.pensjon.selvbetjeningopptjening.opptjening

import no.nav.pensjon.selvbetjeningopptjening.opptjening.grunnlag.Grunnlagstype
import no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.firstDayOf
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.lastDayOf
import java.time.LocalDate

@ConsistentCopyVisibility
data class Beholdning private constructor(
    val id: Long,
    val pid: String? = null,
    val status: String? = null,
    val type: String? = null,
    val beloep: Double,
    val vedtakId: Long,
    val harVedtak: Boolean,
    val fom: LocalDate,
    val tom: LocalDate? = null,
    val harTomDato: Boolean,
    val grunnlag: Double,
    val avkortetGrunnlag: Double,
    val innskudd: Double,
    val harInnskudd: Boolean,
    val innskuddUtenOmsorg: Double,
    val oppdateringAarsak: String? = null,
    val loennsvekstregulering: Loennsvekstregulering? = null,
    val inntektsopptjening: Inntektsopptjening? = null,
    val omsorgsopptjening: Omsorgsopptjening? = null,
    val dagpengeopptjening: Dagpengeopptjening? = null,
    val foerstegangstjenesteopptjening: Foerstegangstjenesteopptjening? = null,
    val ufoereopptjening: Ufoereopptjening? = null,
) : Periode {
    constructor(
        id: Long? = null,
        pid: String? = null,
        status: String? = null,
        type: String? = null,
        beloep: Double? = null,
        vedtakId: Long? = null,
        fom: LocalDate,
        tom: LocalDate? = null,
        grunnlag: Double? = null,
        avkortetGrunnlag: Double? = null,
        innskudd: Double? = null,
        innskuddUtenOmsorg: Double? = null,
        oppdateringAarsak: String? = null,
        loennsvekstregulering: Loennsvekstregulering? = null,
        inntektsopptjening: Inntektsopptjening? = null,
        omsorgsopptjening: Omsorgsopptjening? = null,
        dagpengeopptjening: Dagpengeopptjening? = null,
        foerstegangstjenesteopptjening: Foerstegangstjenesteopptjening? = null,
        ufoereopptjening: Ufoereopptjening? = null,
    ) : this(
        id = id ?: 0L,
        pid = pid,
        status = status,
        type = type,
        beloep = beloep ?: 0.0,
        vedtakId = vedtakId ?: 0L,
        harVedtak = vedtakId != null,
        fom = fom,
        tom = tom,
        harTomDato = tom != null,
        grunnlag = grunnlag ?: 0.0,
        avkortetGrunnlag = avkortetGrunnlag ?: 0.0,
        innskudd = innskudd ?: 0.0,
        harInnskudd = innskudd != null,
        innskuddUtenOmsorg = innskuddUtenOmsorg ?: 0.0,
        oppdateringAarsak = oppdateringAarsak,
        loennsvekstregulering = loennsvekstregulering,
        inntektsopptjening = inntektsopptjening,
        omsorgsopptjening = omsorgsopptjening,
        dagpengeopptjening = dagpengeopptjening,
        foerstegangstjenesteopptjening = foerstegangstjenesteopptjening,
        ufoereopptjening = ufoereopptjening
    )

    override fun getFomDato(): LocalDate = fom

    override fun getTomDato(): LocalDate? = tom

    fun effektivtInnskudd(aar: Int): Double =
        when {
            harInnskudd.not() -> 0.0
            aar == REFORM_2010 -> innskudd + loennsvekstreguleringsbeloep()
            else -> innskudd
        }

    fun loennsvekstreguleringsbeloep(): Double =
         if (harLoennsvekstreguleringsbeloep()) loennsvekstregulering!!.beloep else 0.0

    fun harLoennsvekstreguleringsbeloep(): Boolean =
        loennsvekstregulering?.let {
            it.harBeloep && it.reguleringsdato != null
        } == true

    fun isOmsorgGrunnlagForBeholdning(): Boolean =
        grunnlag == omsorgsopptjening?.beloep

    fun startsFirstDayOf(aar: Int): Boolean =
        fom.isEqual(firstDayOf(aar))

    fun endsLastDayOf(aar: Int): Boolean =
        tom?.isEqual(lastDayOf(aar)) == true

    fun isWithinInclusive(from: LocalDate, to: LocalDate?): Boolean =
        fom.compareTo(from) > -1
                && (harTomDato.not() && (to == null || to.compareTo(fom) > -1)
                || (harTomDato && (to == null || tom!!.compareTo(to) < 1)))

    fun ufoeregrad(): Int? =
        ufoereopptjening?.ufoeregrad

    fun opptjeningsgrunnlagTypeListe(): List<Grunnlagstype> {
        val typer: MutableList<Grunnlagstype> = mutableListOf()

        if ((inntektsopptjening?.beloep ?: 0.0) > 0) {
            typer.add(Grunnlagstype.INNTEKT_GRUNNLAG)
        }

        if ((omsorgsopptjening?.beloep ?: 0.0) > 0) {
            typer.add(Grunnlagstype.OMSORGSOPPTJENING_GRUNNLAG)
        }

        if ((ufoereopptjening?.beloep ?: 0.0) > 0) {
            typer.add(Grunnlagstype.UFORE_GRUNNLAG)
        }

        if ((foerstegangstjenesteopptjening?.beloep ?: 0.0) > 0) {
            typer.add(Grunnlagstype.FORSTEGANGSTJENESTE_GRUNNLAG)
        }

        if (dagpengeopptjening?.harPositivtBeloep() == true) {
            typer.add(Grunnlagstype.DAGPENGER_GRUNNLAG)
        }

        return filterGrunnlagOnlyThoseThatApply(typer)
    }

    private fun filterGrunnlagOnlyThoseThatApply(grunnlagstyper: MutableList<Grunnlagstype>): List<Grunnlagstype> {
        if (grunnlag == 0.0)
            return listOf(Grunnlagstype.NO_GRUNNLAG)

        if (grunnlagstyper.contains(Grunnlagstype.OMSORGSOPPTJENING_GRUNNLAG) && grunnlag == omsorgsopptjening?.beloep)
            return listOf(Grunnlagstype.OMSORGSOPPTJENING_GRUNNLAG)

        if (grunnlagstyper.contains(Grunnlagstype.UFORE_GRUNNLAG) ||
            grunnlagstyper.contains(Grunnlagstype.FORSTEGANGSTJENESTE_GRUNNLAG) ||
            grunnlagstyper.contains(Grunnlagstype.DAGPENGER_GRUNNLAG)
        ) {
            grunnlagstyper.remove(Grunnlagstype.OMSORGSOPPTJENING_GRUNNLAG)
            return grunnlagstyper
        }

        if (grunnlagstyper.contains(Grunnlagstype.INNTEKT_GRUNNLAG) && grunnlag == inntektsopptjening?.beloep)
            return listOf(Grunnlagstype.INNTEKT_GRUNNLAG)

        return if (grunnlagstyper.isEmpty()) listOf(Grunnlagstype.NO_GRUNNLAG) else grunnlagstyper
    }

    companion object {
        val ingen = Beholdning(
            pid = "",
            status = "",
            type = "",
            beloep = 0.0,
            fom = LocalDate.MIN,
            tom = LocalDate.MIN,
            oppdateringAarsak = ""
        )
    }
}