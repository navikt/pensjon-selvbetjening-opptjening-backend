package no.nav.pensjon.selvbetjeningopptjening.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DagpengerDto {

    private Long dagpengerId;
    private String fnr;
    private String dagpengerType;
    private String rapportType;
    private String kilde;
    private Integer ar;
    private Integer utbetalteDagpenger;
    private Integer uavkortetDagpengegrunnlag;
    private Integer ferietillegg;
    private Integer barnetillegg;

    public Long getDagpengerId() {
        return dagpengerId;
    }

    public void setDagpengerId(Long dagpengerId) {
        this.dagpengerId = dagpengerId;
    }

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getDagpengerType() {
        return dagpengerType;
    }

    public void setDagpengerType(String dagpengerType) {
        this.dagpengerType = dagpengerType;
    }

    public String getRapportType() {
        return rapportType;
    }

    public void setRapportType(String rapportType) {
        this.rapportType = rapportType;
    }

    public String getKilde() {
        return kilde;
    }

    public void setKilde(String kilde) {
        this.kilde = kilde;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Integer getUtbetalteDagpenger() {
        return utbetalteDagpenger;
    }

    public void setUtbetalteDagpenger(Integer utbetalteDagpenger) {
        this.utbetalteDagpenger = utbetalteDagpenger;
    }

    public Integer getUavkortetDagpengegrunnlag() {
        return uavkortetDagpengegrunnlag;
    }

    public void setUavkortetDagpengegrunnlag(Integer uavkortetDagpengegrunnlag) {
        this.uavkortetDagpengegrunnlag = uavkortetDagpengegrunnlag;
    }

    public Integer getFerietillegg() {
        return ferietillegg;
    }

    public void setFerietillegg(Integer ferietillegg) {
        this.ferietillegg = ferietillegg;
    }

    public Integer getBarnetillegg() {
        return barnetillegg;
    }

    public void setBarnetillegg(Integer barnetillegg) {
        this.barnetillegg = barnetillegg;
    }
}
