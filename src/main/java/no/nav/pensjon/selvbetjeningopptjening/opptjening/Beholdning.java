package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.*;

public class Beholdning implements Periode {

    private final boolean hasInnskudd;
    private final boolean hasVedtak;
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
    private final InntektOpptjeningBelop inntektOpptjeningBelop;
    private final OmsorgOpptjeningBelop omsorgOpptjeningBelop;
    private final DagpengerOpptjeningBelop dagpengerOpptjeningBelop;
    private final ForstegangstjenesteOpptjeningBelop forstegangstjenesteOpptjeningBelop;
    private final UforeOpptjeningBelop uforeOpptjeningBelop;

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
                      InntektOpptjeningBelop inntektOpptjeningBelop,
                      OmsorgOpptjeningBelop omsorgOpptjeningBelop,
                      DagpengerOpptjeningBelop dagpengerOpptjeningBelop,
                      ForstegangstjenesteOpptjeningBelop forstegangstjenesteOpptjeningBelop,
                      UforeOpptjeningBelop uforeOpptjeningBelop) {
        this.id = id == null ? 0L : id;
        this.fnr = fnr;
        this.status = status;
        this.type = type;
        this.belop = belop == null ? 0D : belop;
        this.vedtakId = vedtakId == null ? 0L : vedtakId;
        this.hasVedtak = vedtakId != null;
        this.fomDato = fomDato;
        this.tomDato = tomDato;
        this.grunnlag = grunnlag == null ? 0D : grunnlag;
        this.grunnlagAvkortet = grunnlagAvkortet == null ? 0D : grunnlagAvkortet;
        this.innskudd = innskudd == null ? 0D : innskudd;
        this.hasInnskudd = innskudd != null;
        this.innskuddUtenOmsorg = innskuddUtenOmsorg == null ? 0D : innskuddUtenOmsorg;
        this.oppdateringArsak = oppdateringArsak;
        this.lonnsvekstregulering = lonnsvekstregulering;
        this.inntektOpptjeningBelop = inntektOpptjeningBelop;
        this.omsorgOpptjeningBelop = omsorgOpptjeningBelop;
        this.dagpengerOpptjeningBelop = dagpengerOpptjeningBelop;
        this.forstegangstjenesteOpptjeningBelop = forstegangstjenesteOpptjeningBelop;
        this.uforeOpptjeningBelop = uforeOpptjeningBelop;
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

    boolean hasInnskudd() {
        return hasInnskudd;
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
        return hasLonnsvekstreguleringsbelop() ? lonnsvekstregulering.getReguleringsbelop() : 0D;
    }

    boolean hasLonnsvekstreguleringsbelop() {
        return lonnsvekstregulering != null && lonnsvekstregulering.getReguleringsbelop() != null;
    }

    InntektOpptjeningBelop getInntektOpptjeningBelop() {
        return inntektOpptjeningBelop;
    }

    OmsorgOpptjeningBelop getOmsorgOpptjeningBelop() {
        return omsorgOpptjeningBelop;
    }

    DagpengerOpptjeningBelop getDagpengerOpptjeningBelop() {
        return dagpengerOpptjeningBelop;
    }

    ForstegangstjenesteOpptjeningBelop getForstegangstjenesteOpptjeningBelop() {
        return forstegangstjenesteOpptjeningBelop;
    }

    UforeOpptjeningBelop getUforeOpptjeningBelop() {
        return uforeOpptjeningBelop;
    }

    List<GrunnlagTypeCode> getOpptjeningGrunnlagTypes() {
        List<GrunnlagTypeCode> grunnlagTypes = new ArrayList<>();

        if (inntektOpptjeningBelop != null && inntektOpptjeningBelop.getBelop() > 0) {
            grunnlagTypes.add(INNTEKT_GRUNNLAG);
        }

        if (omsorgOpptjeningBelop != null && omsorgOpptjeningBelop.getBelop() > 0) {
            grunnlagTypes.add(OMSORGSOPPTJENING_GRUNNLAG);
        }

        if (uforeOpptjeningBelop != null && uforeOpptjeningBelop.getBelop() > 0) {
            grunnlagTypes.add(UFORE_GRUNNLAG);
        }

        if (forstegangstjenesteOpptjeningBelop != null && forstegangstjenesteOpptjeningBelop.getBelop() > 0) {
            grunnlagTypes.add(FORSTEGANGSTJENESTE_GRUNNLAG);
        }

        if (dagpengerOpptjeningBelop != null && (
                dagpengerOpptjeningBelop.getBelopOrdinar() != null && dagpengerOpptjeningBelop.getBelopOrdinar() > 0
                        || dagpengerOpptjeningBelop.getBelopFiskere() != null && dagpengerOpptjeningBelop.getBelopFiskere() > 0)) {
            grunnlagTypes.add(DAGPENGER_GRUNNLAG);
        }

        return filterGrunnlagOnlyThoseThatApply(grunnlagTypes);
    }

    private List<GrunnlagTypeCode> filterGrunnlagOnlyThoseThatApply(List<GrunnlagTypeCode> grunnlagTypes) {
        if (grunnlag == 0D) {
            return List.of(NO_GRUNNLAG);
        }

        if (grunnlagTypes.contains(OMSORGSOPPTJENING_GRUNNLAG) && grunnlag == omsorgOpptjeningBelop.getBelop()) {
            return List.of(OMSORGSOPPTJENING_GRUNNLAG);
        }

        if (grunnlagTypes.contains(UFORE_GRUNNLAG) ||
                grunnlagTypes.contains(FORSTEGANGSTJENESTE_GRUNNLAG) ||
                grunnlagTypes.contains(DAGPENGER_GRUNNLAG)) {
            grunnlagTypes.remove(OMSORGSOPPTJENING_GRUNNLAG);
            return grunnlagTypes;
        }

        if (grunnlagTypes.contains(INNTEKT_GRUNNLAG) && grunnlag == inntektOpptjeningBelop.getBelop()) {
            return List.of(INNTEKT_GRUNNLAG);
        }

        return grunnlagTypes.isEmpty() ? List.of(NO_GRUNNLAG) : grunnlagTypes;
    }
}
