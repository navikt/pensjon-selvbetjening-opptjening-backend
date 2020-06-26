package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;

import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

public class Uforeperiode {
    private Integer uforegrad;
    private UforeTypeCode uforeType;
    private LocalDate uforetidspunktFom;
    private LocalDate uforetidspunktTom;

    public Integer getUforegrad() {
        return uforegrad;
    }

    public void setUforegrad(Integer uforegrad) {
        this.uforegrad = uforegrad;
    }

    public void setUforetidspunktTom(LocalDate uforetidspunktTom) {
        this.uforetidspunktTom = uforetidspunktTom;
    }

    public UforeTypeCode getUforeType() {
        return uforeType;
    }

    public void setUforeType(UforeTypeCode uforeType) {
        this.uforeType = uforeType;
    }

    public LocalDate getUforetidspunktFom() {
        return uforetidspunktFom;
    }

    public void setUforetidspunktFom(LocalDate uforetidspunktFom) {
        this.uforetidspunktFom = uforetidspunktFom;
    }

    public LocalDate getUforetidspunktTom() {
        return uforetidspunktTom;
    }
}
