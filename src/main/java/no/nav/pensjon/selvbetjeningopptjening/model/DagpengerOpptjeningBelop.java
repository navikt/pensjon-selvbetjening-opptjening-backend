package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DagpengerOpptjeningBelop{

    private Long dagpengerOpptjeningBelopId;

    private Integer ar;

    private Double belopOrdinar;

    private Double belopFiskere;

    private List<Dagpenger> dagpengerListe = new ArrayList<Dagpenger>();

    public Long getDagpengerOpptjeningBelopId() {
        return dagpengerOpptjeningBelopId;
    }

    public void setDagpengerOpptjeningBelopId(Long dagpengerOpptjeningBelopId) {
        this.dagpengerOpptjeningBelopId = dagpengerOpptjeningBelopId;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Double getBelopOrdinar() {
        return belopOrdinar;
    }

    public void setBelopOrdinar(Double belopOrdinar) {
        this.belopOrdinar = belopOrdinar;
    }

    public Double getBelopFiskere() {
        return belopFiskere;
    }

    public void setBelopFiskere(Double belopFiskere) {
        this.belopFiskere = belopFiskere;
    }

    public List<Dagpenger> getDagpengerListe() {
        return dagpengerListe;
    }

    public void setDagpengerListe(List<Dagpenger> dagpengerListe) {
        this.dagpengerListe = dagpengerListe;
    }
}
