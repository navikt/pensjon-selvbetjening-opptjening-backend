package no.nav.pensjon.selvbetjeningopptjening.consumers;

import java.io.Serializable;


public class Omsorg implements Serializable {

    private Long omsorgId;

    private String fnr;

    private String fnrOmsorgFor;

    private String omsorgType;

    private String kilde;

    private Integer ar;

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

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Long getOmsorgId() {
        return omsorgId;
    }

    public void setOmsorgId(Long omsorgId) {
        this.omsorgId = omsorgId;
    }

    public String getOmsorgType() {
        return omsorgType;
    }

    public void setOmsorgType(String omsorgType) {
        this.omsorgType = omsorgType;
    }

    public String getKilde() {
        return kilde;
    }

    public void setKilde(String kilde) {
        this.kilde = kilde;
    }
}
