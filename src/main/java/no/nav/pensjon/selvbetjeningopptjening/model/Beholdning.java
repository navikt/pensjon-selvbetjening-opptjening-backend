package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Beholdning {
    private Long beholdningId;

    private String fnr;

    private String status;

    private String beholdningType;

    private Double belop;

    private Long vedtakId;

    private LocalDate fomDato;

    private LocalDate tomDato;

    private Double beholdningGrunnlag;

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

    public Long getBeholdningId() {
        return beholdningId;
    }

    public void setBeholdningId(Long beholdningId) {
        this.beholdningId = beholdningId;
    }

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBeholdningType() {
        return beholdningType;
    }

    public void setBeholdningType(String beholdningType) {
        this.beholdningType = beholdningType;
    }

    public Double getBelop() {
        return belop;
    }

    public void setBelop(Double belop) {
        this.belop = belop;
    }

    public Long getVedtakId() {
        return vedtakId;
    }

    public void setVedtakId(Long vedtakId) {
        this.vedtakId = vedtakId;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public void setFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public void setTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
    }

    public Double getBeholdningGrunnlag() {
        return beholdningGrunnlag;
    }

    public void setBeholdningGrunnlag(Double beholdningGrunnlag) {
        this.beholdningGrunnlag = beholdningGrunnlag;
    }

    public Double getBeholdningGrunnlagAvkortet() {
        return beholdningGrunnlagAvkortet;
    }

    public void setBeholdningGrunnlagAvkortet(Double beholdningGrunnlagAvkortet) {
        this.beholdningGrunnlagAvkortet = beholdningGrunnlagAvkortet;
    }

    public Double getBeholdningInnskudd() {
        return beholdningInnskudd;
    }

    public void setBeholdningInnskudd(Double beholdningInnskudd) {
        this.beholdningInnskudd = beholdningInnskudd;
    }

    public Double getBeholdningInnskuddUtenOmsorg() {
        return beholdningInnskuddUtenOmsorg;
    }

    public void setBeholdningInnskuddUtenOmsorg(Double beholdningInnskuddUtenOmsorg) {
        this.beholdningInnskuddUtenOmsorg = beholdningInnskuddUtenOmsorg;
    }

    public String getOppdateringArsak() {
        return oppdateringArsak;
    }

    public void setOppdateringArsak(String oppdateringArsak) {
        this.oppdateringArsak = oppdateringArsak;
    }

    public Lonnsvekstregulering getLonnsvekstregulering() {
        return lonnsvekstregulering;
    }

    public void setLonnsvekstregulering(Lonnsvekstregulering lonnsvekstregulering) {
        this.lonnsvekstregulering = lonnsvekstregulering;
    }

    public InntektOpptjeningBelop getInntektOpptjeningBelop() {
        return inntektOpptjeningBelop;
    }

    public void setInntektOpptjeningBelop(InntektOpptjeningBelop inntektOpptjeningBelop) {
        this.inntektOpptjeningBelop = inntektOpptjeningBelop;
    }

    public OmsorgOpptjeningBelop getOmsorgOpptjeningBelop() {
        return omsorgOpptjeningBelop;
    }

    public void setOmsorgOpptjeningBelop(OmsorgOpptjeningBelop omsorgOpptjeningBelop) {
        this.omsorgOpptjeningBelop = omsorgOpptjeningBelop;
    }

    public DagpengerOpptjeningBelop getDagpengerOpptjeningBelop() {
        return dagpengerOpptjeningBelop;
    }

    public void setDagpengerOpptjeningBelop(DagpengerOpptjeningBelop dagpengerOpptjeningBelop) {
        this.dagpengerOpptjeningBelop = dagpengerOpptjeningBelop;
    }

    public ForstegangstjenesteOpptjeningBelop getForstegangstjenesteOpptjeningBelop() {
        return forstegangstjenesteOpptjeningBelop;
    }

    public void setForstegangstjenesteOpptjeningBelop(ForstegangstjenesteOpptjeningBelop forstegangstjenesteOpptjeningBelop) {
        this.forstegangstjenesteOpptjeningBelop = forstegangstjenesteOpptjeningBelop;
    }

    public UforeOpptjeningBelop getUforeOpptjeningBelop() {
        return uforeOpptjeningBelop;
    }

    public void setUforeOpptjeningBelop(UforeOpptjeningBelop uforeOpptjeningBelop) {
        this.uforeOpptjeningBelop = uforeOpptjeningBelop;
    }
}
