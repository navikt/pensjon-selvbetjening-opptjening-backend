package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForstegangstjenestePeriode {
    private Long forstegangstjenestePeriodeId;
    private String periodeType;
    private String tjenesteType;
    private Date fomDato;
    private Date tomDato;

    public Long getForstegangstjenestePeriodeId() {
        return forstegangstjenestePeriodeId;
    }

    public void setForstegangstjenestePeriodeId(Long forstegangstjenestePeriodeId) {
        this.forstegangstjenestePeriodeId = forstegangstjenestePeriodeId;
    }

    public String getPeriodeType() {
        return periodeType;
    }

    public void setPeriodeType(String periodeType) {
        this.periodeType = periodeType;
    }

    public String getTjenesteType() {
        return tjenesteType;
    }

    public void setTjenesteType(String tjenesteType) {
        this.tjenesteType = tjenesteType;
    }

    public Date getFomDato() {
        return fomDato;
    }

    public void setFomDato(Date fomDato) {
        this.fomDato = fomDato;
    }

    public Date getTomDato() {
        return tomDato;
    }

    public void setTomDato(Date tomDato) {
        this.tomDato = tomDato;
    }
}
