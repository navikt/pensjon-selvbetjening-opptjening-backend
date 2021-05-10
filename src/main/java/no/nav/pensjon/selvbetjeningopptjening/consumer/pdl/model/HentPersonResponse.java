package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class HentPersonResponse {
    private List<Foedsel> foedsel;
    private List<Navn> navn;

    public List<Foedsel> getFoedsel() {
        return foedsel;
    }

    public void setFoedsel(List<Foedsel> foedsel) {
        this.foedsel = foedsel;
    }

    public List<Navn> getNavn() {
        return navn;
    }

    public void setNavn(List<Navn> navn) {
        this.navn = navn;
    }
}
