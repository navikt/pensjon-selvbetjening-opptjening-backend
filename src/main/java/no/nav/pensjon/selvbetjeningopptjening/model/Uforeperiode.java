package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Uforeperiode {
    private Integer uforegrad;
    private UforeTypeCode uforetype;
    private LocalDate ufgFom;
    private LocalDate ufgTom;

    public Integer getUforegrad() {
        return uforegrad;
    }

    public void setUforegrad(Integer uforegrad) {
        this.uforegrad = uforegrad;
    }

    public void setUfgTom(LocalDate ufgTom) {
        this.ufgTom = ufgTom;
    }

    public UforeTypeCode getUforetype() {
        return uforetype;
    }

    public void setUforetype(UforeTypeCode uforetype) {
        this.uforetype = uforetype;
    }

    public LocalDate getUfgFom() {
        return ufgFom;
    }

    public void setUfgFom(LocalDate ufgFom) {
        this.ufgFom = ufgFom;
    }

    public LocalDate getUfgTom() {
        return ufgTom;
    }
}
