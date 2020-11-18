package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.*;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.firstDayOf;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.lastDayOf;

public class Beholdning implements Periode {

    private final boolean hasInnskudd;
    private final boolean hasVedtak;
    private final boolean hasTomDato;
    private final long id;
    private final String fnr;
    private final String status;
    private final String type;
    private final double belop;
    private final long vedtakId;
    private final LocalDate fomDato;
    private final LocalDate tomDato;
    private final double grunnlag;
    private final double grunnlagAvkortet;
    private final double innskudd;
    private final double innskuddUtenOmsorg;
    private final String oppdateringArsak;
    private final Lonnsvekstregulering lonnsvekstregulering;
    private final Inntektsopptjening inntektsopptjening;
    private final Omsorgsopptjening omsorgsopptjening;
    private final Dagpengeopptjening dagpengeopptjening;
    private final Forstegangstjenesteopptjening forstegangstjenesteopptjening;
    private final Uforeopptjening uforeopptjening;

    public Beholdning(Long id,
                      String fnr,
                      String status,
                      String type,
                      Double belop,
                      Long vedtakId,
                      LocalDate fomDato,
                      LocalDate tomDato,
                      Double grunnlag,
                      Double grunnlagAvkortet,
                      Double innskudd,
                      Double innskuddUtenOmsorg,
                      String oppdateringArsak,
                      Lonnsvekstregulering lonnsvekstregulering,
                      Inntektsopptjening inntektsopptjening,
                      Omsorgsopptjening omsorgsopptjening,
                      Dagpengeopptjening dagpengeopptjening,
                      Forstegangstjenesteopptjening forstegangstjenesteopptjening,
                      Uforeopptjening uforeopptjening) {
        this.id = id == null ? 0L : id;
        this.fnr = fnr;
        this.status = status;
        this.type = type;
        this.belop = belop == null ? 0D : belop;
        this.vedtakId = vedtakId == null ? 0L : vedtakId;
        this.hasVedtak = vedtakId != null;
        this.fomDato = requireNonNull(fomDato);
        this.tomDato = tomDato;
        this.hasTomDato = tomDato != null;
        this.grunnlag = grunnlag == null ? 0D : grunnlag;
        this.grunnlagAvkortet = grunnlagAvkortet == null ? 0D : grunnlagAvkortet;
        this.innskudd = innskudd == null ? 0D : innskudd;
        this.hasInnskudd = innskudd != null;
        this.innskuddUtenOmsorg = innskuddUtenOmsorg == null ? 0D : innskuddUtenOmsorg;
        this.oppdateringArsak = oppdateringArsak;
        this.lonnsvekstregulering = lonnsvekstregulering;
        this.inntektsopptjening = inntektsopptjening;
        this.omsorgsopptjening = omsorgsopptjening;
        this.dagpengeopptjening = dagpengeopptjening;
        this.forstegangstjenesteopptjening = forstegangstjenesteopptjening;
        this.uforeopptjening = uforeopptjening;
    }

    long getId() {
        return id;
    }

    public String getFnr() {
        return fnr;
    }

    String getStatus() {
        return status;
    }

    String getType() {
        return type;
    }

    public double getBelop() {
        return belop;
    }

    long getVedtakId() {
        return vedtakId;
    }

    boolean hasVedtak() {
        return hasVedtak;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    double getGrunnlag() {
        return grunnlag;
    }

    double getGrunnlagAvkortet() {
        return grunnlagAvkortet;
    }

    double getInnskudd() {
        return innskudd;
    }

    double getInnskuddUtenOmsorg() {
        return innskuddUtenOmsorg;
    }

    String getOppdateringArsak() {
        return oppdateringArsak;
    }

    Lonnsvekstregulering getLonnsvekstregulering() {
        return lonnsvekstregulering;
    }

    double getLonnsvekstreguleringsbelop() {
        return hasLonnsvekstreguleringsbelop() ? lonnsvekstregulering.getBelop() : 0D;
    }

    boolean hasLonnsvekstreguleringsbelop() {
        return lonnsvekstregulering != null && lonnsvekstregulering.hasBelop();
    }

    Inntektsopptjening getInntektsopptjening() {
        return inntektsopptjening;
    }

    Omsorgsopptjening getOmsorgsopptjening() {
        return omsorgsopptjening;
    }

    Dagpengeopptjening getDagpengeopptjening() {
        return dagpengeopptjening;
    }

    Forstegangstjenesteopptjening getForstegangstjenesteopptjening() {
        return forstegangstjenesteopptjening;
    }

    Uforeopptjening getUforeopptjening() {
        return uforeopptjening;
    }

    boolean startsFirstDayOf(int year) {
        return fomDato.isEqual(firstDayOf(year));
    }

    boolean endsLastDayOf(int year) {
        return hasTomDato && tomDato.isEqual(lastDayOf(year));
    }

    boolean isWithinInclusive(LocalDate from, LocalDate to) {
        return fomDato.compareTo(from) > -1
                && (!hasTomDato && (to == null || to.compareTo(fomDato) > -1)
                || (hasTomDato && (to == null || tomDato.compareTo(to) < 1)));
    }

    double getEffectiveInnskudd(int year) {
        if (!hasInnskudd) {
            return 0D;
        }

        return year == REFORM_2010
                ? innskudd + getLonnsvekstreguleringsbelop()
                : innskudd;
    }

    List<GrunnlagTypeCode> getOpptjeningGrunnlagTypes() {
        List<GrunnlagTypeCode> grunnlagTypes = new ArrayList<>();

        if (inntektsopptjening != null && inntektsopptjening.getBelop() > 0) {
            grunnlagTypes.add(INNTEKT_GRUNNLAG);
        }

        if (omsorgsopptjening != null && omsorgsopptjening.getBelop() > 0) {
            grunnlagTypes.add(OMSORGSOPPTJENING_GRUNNLAG);
        }

        if (uforeopptjening != null && uforeopptjening.getBelop() > 0) {
            grunnlagTypes.add(UFORE_GRUNNLAG);
        }

        if (forstegangstjenesteopptjening != null && forstegangstjenesteopptjening.getBelop() > 0) {
            grunnlagTypes.add(FORSTEGANGSTJENESTE_GRUNNLAG);
        }

        if (dagpengeopptjening != null && dagpengeopptjening.hasPositiveBelop()) {
            grunnlagTypes.add(DAGPENGER_GRUNNLAG);
        }

        return filterGrunnlagOnlyThoseThatApply(grunnlagTypes);
    }

    Integer getUforegrad() {
        return uforeopptjening == null ? null : uforeopptjening.getUforegrad();
    }

    private List<GrunnlagTypeCode> filterGrunnlagOnlyThoseThatApply(List<GrunnlagTypeCode> grunnlagTypes) {
        if (grunnlag == 0D) {
            return List.of(NO_GRUNNLAG);
        }

        if (grunnlagTypes.contains(OMSORGSOPPTJENING_GRUNNLAG) && grunnlag == omsorgsopptjening.getBelop()) {
            return List.of(OMSORGSOPPTJENING_GRUNNLAG);
        }

        if (grunnlagTypes.contains(UFORE_GRUNNLAG) ||
                grunnlagTypes.contains(FORSTEGANGSTJENESTE_GRUNNLAG) ||
                grunnlagTypes.contains(DAGPENGER_GRUNNLAG)) {
            grunnlagTypes.remove(OMSORGSOPPTJENING_GRUNNLAG);
            return grunnlagTypes;
        }

        if (grunnlagTypes.contains(INNTEKT_GRUNNLAG) && grunnlag == inntektsopptjening.getBelop()) {
            return List.of(INNTEKT_GRUNNLAG);
        }

        return grunnlagTypes.isEmpty() ? List.of(NO_GRUNNLAG) : grunnlagTypes;
    }

    static final Beholdning NULL = new Beholdning(
            null,
            "",
            "",
            "",
            null,
            null, // use null (not 0), so that hasVedtak is set correctly
            LocalDate.MIN,
            LocalDate.MIN,
            null,
            null,
            null, // use null (not 0), so that hasInnskudd is set correctly
            null,
            "",
            null,
            null,
            null,
            null,
            null,
            null);
}
