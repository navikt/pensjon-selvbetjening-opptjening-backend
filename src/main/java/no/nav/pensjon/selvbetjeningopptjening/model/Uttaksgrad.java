package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;

public class Uttaksgrad {
    private LocalDate fomDato;
    private LocalDate tomDato;
    private Integer uttaksgrad;

    public LocalDate getFomDato() {
        return fomDato;
    }

    public void setFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public void setTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
    }

    public Integer getUttaksgrad() {
        return uttaksgrad;
    }

    public void setUttaksgrad(Integer uttaksgrad) {
        this.uttaksgrad = uttaksgrad;
    }
}
