package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.*;

public class Beholdning {

    private Long beholdningId;
    private String fnr;
    private String status;
    private String beholdningType;
    private double belop;
    private Long vedtakId;
    private LocalDate fomDato;
    private LocalDate tomDato;
    private double beholdningGrunnlag;
    private Double beholdningGrunnlagAvkortet;
    private Double beholdningInnskudd;
    private Double beholdningInnskuddUtenOmsorg;
    private String oppdateringArsak;
    private Lonnsvekstregulering lonnsvekstregulering;
    private InntektOpptjeningBelop inntektOpptjeningBelop;
    private OmsorgOpptjeningBelop omsorgOpptjeningBelop;
    private DagpengerOpptjeningBelop dagpengerOpptjeningBelop;
    private ForstegangstjenesteOpptjeningBelop forstegangstjenesteOpptjeningBelop;
    private UforeOpptjeningBelop uforeOpptjeningBelop;

    public Beholdning(Long beholdningId,
                      String fnr,
                      String status,
                      String beholdningType,
                      Double belop,
                      Long vedtakId,
                      LocalDate fomDato,
                      LocalDate tomDato,
                      Double beholdningGrunnlag,
                      Double beholdningGrunnlagAvkortet,
                      Double beholdningInnskudd,
                      Double beholdningInnskuddUtenOmsorg,
                      String oppdateringArsak,
                      Lonnsvekstregulering lonnsvekstregulering,
                      InntektOpptjeningBelop inntektOpptjeningBelop,
                      OmsorgOpptjeningBelop omsorgOpptjeningBelop,
                      DagpengerOpptjeningBelop dagpengerOpptjeningBelop,
                      ForstegangstjenesteOpptjeningBelop forstegangstjenesteOpptjeningBelop,
                      UforeOpptjeningBelop uforeOpptjeningBelop) {
        this.beholdningId = beholdningId;
        this.fnr = fnr;
        this.status = status;
        this.beholdningType = beholdningType;
        this.belop = belop == null ? 0D : belop;
        this.vedtakId = vedtakId;
        this.fomDato = fomDato;
        this.tomDato = tomDato;
        this.beholdningGrunnlag = beholdningGrunnlag == null ? 0D : beholdningGrunnlag;
        this.beholdningGrunnlagAvkortet = beholdningGrunnlagAvkortet;
        this.beholdningInnskudd = beholdningInnskudd;
        this.beholdningInnskuddUtenOmsorg = beholdningInnskuddUtenOmsorg;
        this.oppdateringArsak = oppdateringArsak;
        this.lonnsvekstregulering = lonnsvekstregulering;
        this.inntektOpptjeningBelop = inntektOpptjeningBelop;
        this.omsorgOpptjeningBelop = omsorgOpptjeningBelop;
        this.dagpengerOpptjeningBelop = dagpengerOpptjeningBelop;
        this.forstegangstjenesteOpptjeningBelop = forstegangstjenesteOpptjeningBelop;
        this.uforeOpptjeningBelop = uforeOpptjeningBelop;
    }

    public Beholdning(LocalDate fomDato, double belop) {
        this.fomDato = fomDato;
        this.belop = belop;
    }

    public Long getBeholdningId() {
        return beholdningId;
    }

    public String getFnr() {
        return fnr;
    }

    public String getStatus() {
        return status;
    }

    public String getBeholdningType() {
        return beholdningType;
    }

    public Double getBelop() {
        return belop;
    }

    public Long getVedtakId() {
        return vedtakId;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public Double getBeholdningGrunnlag() {
        return beholdningGrunnlag;
    }

    public Double getBeholdningGrunnlagAvkortet() {
        return beholdningGrunnlagAvkortet;
    }

    public Double getBeholdningInnskudd() {
        return beholdningInnskudd;
    }

    public Double getBeholdningInnskuddUtenOmsorg() {
        return beholdningInnskuddUtenOmsorg;
    }

    public String getOppdateringArsak() {
        return oppdateringArsak;
    }

    public Lonnsvekstregulering getLonnsvekstregulering() {
        return lonnsvekstregulering;
    }

    public InntektOpptjeningBelop getInntektOpptjeningBelop() {
        return inntektOpptjeningBelop;
    }

    public OmsorgOpptjeningBelop getOmsorgOpptjeningBelop() {
        return omsorgOpptjeningBelop;
    }

    public DagpengerOpptjeningBelop getDagpengerOpptjeningBelop() {
        return dagpengerOpptjeningBelop;
    }

    public ForstegangstjenesteOpptjeningBelop getForstegangstjenesteOpptjeningBelop() {
        return forstegangstjenesteOpptjeningBelop;
    }

    public UforeOpptjeningBelop getUforeOpptjeningBelop() {
        return uforeOpptjeningBelop;
    }

    List<GrunnlagTypeCode> getOpptjeningGrunnlagTypes() {
        List<GrunnlagTypeCode> presentGrunnlagTypes = new ArrayList<>();

        if (getInntektOpptjeningBelop() != null && getInntektOpptjeningBelop().getBelop() > 0) {
            presentGrunnlagTypes.add(INNTEKT_GRUNNLAG);
        }
        if (getOmsorgOpptjeningBelop() != null && getOmsorgOpptjeningBelop().getBelop() > 0) {
            presentGrunnlagTypes.add(OMSORGSOPPTJENING_GRUNNLAG);
        }
        if (getUforeOpptjeningBelop() != null && getUforeOpptjeningBelop().getBelop() > 0) {
            presentGrunnlagTypes.add(UFORE_GRUNNLAG);
        }
        if (getForstegangstjenesteOpptjeningBelop() != null && getForstegangstjenesteOpptjeningBelop().getBelop() > 0) {
            presentGrunnlagTypes.add(FORSTEGANGSTJENESTE_GRUNNLAG);
        }
        if (getDagpengerOpptjeningBelop() != null && (
                getDagpengerOpptjeningBelop().getBelopOrdinar() != null && getDagpengerOpptjeningBelop().getBelopOrdinar() > 0
                        || getDagpengerOpptjeningBelop().getBelopFiskere() != null && getDagpengerOpptjeningBelop().getBelopFiskere() > 0)) {
            presentGrunnlagTypes.add(DAGPENGER_GRUNNLAG);
        }

        return filterGrunnlagOnlyThoseThatApply(presentGrunnlagTypes);
    }

    private List<GrunnlagTypeCode> filterGrunnlagOnlyThoseThatApply(List<GrunnlagTypeCode> presentGrunnlagTypes) {
        Double grunnlag = getBeholdningGrunnlag();
        if (grunnlag == null || grunnlag.equals(0.0)) {
            return List.of(NO_GRUNNLAG);
        } else if (presentGrunnlagTypes.contains(OMSORGSOPPTJENING_GRUNNLAG) && grunnlag.equals(getOmsorgOpptjeningBelop().getBelop())) {
            return List.of(OMSORGSOPPTJENING_GRUNNLAG);
        } else if (presentGrunnlagTypes.contains(UFORE_GRUNNLAG) ||
                presentGrunnlagTypes.contains(FORSTEGANGSTJENESTE_GRUNNLAG) ||
                presentGrunnlagTypes.contains(DAGPENGER_GRUNNLAG)) {
            presentGrunnlagTypes.remove(OMSORGSOPPTJENING_GRUNNLAG);
            return presentGrunnlagTypes;
        } else if (presentGrunnlagTypes.contains(INNTEKT_GRUNNLAG) && grunnlag.equals(getInntektOpptjeningBelop().getBelop())) {
            return List.of(INNTEKT_GRUNNLAG);
        }
        return presentGrunnlagTypes.isEmpty() ? List.of(NO_GRUNNLAG) : presentGrunnlagTypes;
    }
}
