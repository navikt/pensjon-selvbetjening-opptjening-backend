package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class HentPersonResponse {
    private List<Foedsel> foedsel;
    private List<Navn> navn;

    public HentPersonResponse(List<Foedsel> foedsel, List<Navn> navn) {
        this.foedsel = foedsel;
        this.navn = navn;
    }

    public List<Foedsel> getFoedsel() {
        return foedsel;
    }

    public List<Navn> getNavn() {
        return navn;
    }
}
