package no.nav.pensjon.selvbetjeningopptjening.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeholdningDto {

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
    private LonnsvekstreguleringDto lonnsvekstregulering;
    private InntektOpptjeningBelopDto inntektOpptjeningBelop;
    private OmsorgOpptjeningBelopDto omsorgOpptjeningBelop;
    private DagpengerOpptjeningBelopDto dagpengerOpptjeningBelop;
    private ForstegangstjenesteOpptjeningBelopDto forstegangstjenesteOpptjeningBelop;
    private UforeOpptjeningBelopDto uforeOpptjeningBelop;

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

    public LonnsvekstreguleringDto getLonnsvekstregulering() {
        return lonnsvekstregulering;
    }

    public void setLonnsvekstregulering(LonnsvekstreguleringDto lonnsvekstregulering) {
        this.lonnsvekstregulering = lonnsvekstregulering;
    }

    public InntektOpptjeningBelopDto getInntektOpptjeningBelop() {
        return inntektOpptjeningBelop;
    }

    public void setInntektOpptjeningBelop(InntektOpptjeningBelopDto inntektOpptjeningBelop) {
        this.inntektOpptjeningBelop = inntektOpptjeningBelop;
    }

    public OmsorgOpptjeningBelopDto getOmsorgOpptjeningBelop() {
        return omsorgOpptjeningBelop;
    }

    public void setOmsorgOpptjeningBelop(OmsorgOpptjeningBelopDto omsorgOpptjeningBelop) {
        this.omsorgOpptjeningBelop = omsorgOpptjeningBelop;
    }

    public DagpengerOpptjeningBelopDto getDagpengerOpptjeningBelop() {
        return dagpengerOpptjeningBelop;
    }

    public void setDagpengerOpptjeningBelop(DagpengerOpptjeningBelopDto dagpengerOpptjeningBelop) {
        this.dagpengerOpptjeningBelop = dagpengerOpptjeningBelop;
    }

    public ForstegangstjenesteOpptjeningBelopDto getForstegangstjenesteOpptjeningBelop() {
        return forstegangstjenesteOpptjeningBelop;
    }

    public void setForstegangstjenesteOpptjeningBelop(ForstegangstjenesteOpptjeningBelopDto forstegangstjenesteOpptjeningBelop) {
        this.forstegangstjenesteOpptjeningBelop = forstegangstjenesteOpptjeningBelop;
    }

    public UforeOpptjeningBelopDto getUforeOpptjeningBelop() {
        return uforeOpptjeningBelop;
    }

    public void setUforeOpptjeningBelop(UforeOpptjeningBelopDto uforeOpptjeningBelop) {
        this.uforeOpptjeningBelop = uforeOpptjeningBelop;
    }
}
