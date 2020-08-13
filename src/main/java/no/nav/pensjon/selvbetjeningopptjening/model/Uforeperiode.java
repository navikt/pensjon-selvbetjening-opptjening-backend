package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;

import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

public class Uforeperiode {
    private Integer uforegrad;
    private UforeTypeCode uforeType;
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

    public UforeTypeCode getUforeType() {
        return uforeType;
    }

    public void setUforeType(UforeTypeCode uforeType) {
        this.uforeType = uforeType;
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
