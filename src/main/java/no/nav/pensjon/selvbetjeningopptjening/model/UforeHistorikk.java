package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.ArrayList;
import java.util.List;

public class UforeHistorikk {

    private List<Uforeperiode> uforeperiodeListe = new ArrayList<>();

    public List<Uforeperiode> getUforeperiodeListe() {
        return uforeperiodeListe;
    }

    public void setUforeperiodeListe(List<Uforeperiode> uforeperiodeListe) {
        this.uforeperiodeListe = uforeperiodeListe;
    }
}
