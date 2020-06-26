package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OmsorgOpptjeningBelop {

    private Long omsorgOpptjeningBelopId;

    private Integer ar;

    private Double belop;

    private Double omsorgOpptjeningInnskudd;

    private List<Omsorg> omsorgListe = new ArrayList<Omsorg>();

    public Long getOmsorgOpptjeningBelopId() {
        return omsorgOpptjeningBelopId;
    }

    public void setOmsorgOpptjeningBelopId(Long omsorgOpptjeningBelopId) {
        this.omsorgOpptjeningBelopId = omsorgOpptjeningBelopId;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Double getBelop() {
        return belop;
    }

    public void setBelop(Double belop) {
        this.belop = belop;
    }

    public Double getOmsorgOpptjeningInnskudd() {
        return omsorgOpptjeningInnskudd;
    }

    public void setOmsorgOpptjeningInnskudd(Double omsorgOpptjeningInnskudd) {
        this.omsorgOpptjeningInnskudd = omsorgOpptjeningInnskudd;
    }

    public List<Omsorg> getOmsorgListe() {
        return omsorgListe;
    }

    public void setOmsorgListe(List<Omsorg> omsorgListe) {
        this.omsorgListe = omsorgListe;
    }
}
