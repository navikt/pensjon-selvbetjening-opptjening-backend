package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class HentPersonResponse {
    private List<Foedsel> foedsel;

    public List<Foedsel> getFoedsel() {
        return foedsel;
    }

    public void setFoedsel(List<Foedsel> foedsel) {
        this.foedsel = foedsel;
    }
}
