package no.nav.pensjon.selvbetjeningopptjening.model;


import java.io.Serializable;

/**
 * Non-persistent class used to represent Inntekt.
 */
public class Inntekt implements Serializable {

    private Long inntektId;

    private String fnr;

    private String kilde;

    private String kommune;

    private String piMerke;

    private Integer inntektAr;

    private Long belop;

    private String inntektType;

    public Inntekt() {
    }


    public Long getBelop() {
        return belop;
    }


    public void setBelop(Long belop) {
        this.belop = belop;
    }

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public Integer getInntektAr() {
        return inntektAr;
    }

    public void setInntektAr(Integer inntektAr) {
        this.inntektAr = inntektAr;
    }

    public Long getInntektId() {
        return inntektId;
    }

    public void setInntektId(Long inntektId) {
        this.inntektId = inntektId;
    }

    public String getKilde() {
        return kilde;
    }

    public void setKilde(String kilde) {
        this.kilde = kilde;
    }

    public String getKommune() {
        return kommune;
    }

    public void setKommune(String kommune) {
        this.kommune = kommune;
    }

    public String getPiMerke() {
        return piMerke;
    }

    public void setPiMerke(String piMerke) {
        this.piMerke = piMerke;
    }

    public String getInntektType() {
        return inntektType;
    }

    public void setInntektType(String inntektType) {
        this.inntektType = inntektType;
    }
}
