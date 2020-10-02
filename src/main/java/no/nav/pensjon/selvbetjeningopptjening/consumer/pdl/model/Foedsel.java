package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.time.LocalDate;

public class Foedsel {

    private LocalDate foedselsdato;
    private Integer foedselsaar;

    public LocalDate getFoedselsdato() {
        return foedselsdato;
    }

    public Integer getFoedselsaar() {
        return foedselsaar;
    }

    public void setFoedselsdato(LocalDate foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public void setFoedselsaar(Integer foedselsaar) {
        this.foedselsaar = foedselsaar;
    }
}
