package no.nav.pensjon.selvbetjeningopptjening.model;

import java.io.Serializable;

public class Pensjonspoeng implements Serializable {

    private Long pensjonspoengId;

    private String fnr;

    private String fnrOmsorgFor;

    private String kilde;

    private String pensjonspoengType;

    private Inntekt inntekt;

    private Omsorg omsorg;

    private Integer ar;

    private Integer anvendtPi;

    private Double poeng;

    private Integer maxUforegrad;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getFnrOmsorgFor() {
        return fnrOmsorgFor;
    }

    public void setFnrOmsorgFor(String fnrOmsorgFor) {
        this.fnrOmsorgFor = fnrOmsorgFor;
    }

    public Integer getAnvendtPi() {
        return anvendtPi;
    }

    public void setAnvendtPi(Integer anvendtPi) {
        this.anvendtPi = anvendtPi;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Inntekt getInntekt() {
        return inntekt;
    }

    public void setInntekt(Inntekt inntekt) {
        this.inntekt = inntekt;
    }

    public Integer getMaxUforegrad() {
        return maxUforegrad;
    }

    public void setMaxUforegrad(Integer maxUforegrad) {
        this.maxUforegrad = maxUforegrad;
    }

    public Omsorg getOmsorg() {
        return omsorg;
    }

    public void setOmsorg(Omsorg omsorg) {
        this.omsorg = omsorg;
    }

    public Long getPensjonspoengId() {
        return pensjonspoengId;
    }

    public void setPensjonspoengId(Long pensjonspoengId) {
        this.pensjonspoengId = pensjonspoengId;
    }

    public Double getPoeng() {
        return poeng;
    }

    public void setPoeng(Double poeng) {
        this.poeng = poeng;
    }

    public String getKilde() {
        return kilde;
    }

    public void setKilde(String kilde) {
        this.kilde = kilde;
    }

    public String getPensjonspoengType() {
        return pensjonspoengType;
    }

    public void setPensjonspoengType(String pensjonspoengType) {
        this.pensjonspoengType = pensjonspoengType;
    }
}
