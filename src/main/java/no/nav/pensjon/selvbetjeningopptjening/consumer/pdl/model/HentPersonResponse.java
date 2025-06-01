package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class HentPersonResponse {
    private List<Foedselsdato> foedselsdato;
    private List<Navn> navn;

    public List<Foedselsdato> getFoedselsdato() {
        return foedselsdato;
    }

    public void setFoedselsdato(List<Foedselsdato> foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public List<Navn> getNavn() {
        return navn;
    }

    public void setNavn(List<Navn> navn) {
        this.navn = navn;
    }
}
