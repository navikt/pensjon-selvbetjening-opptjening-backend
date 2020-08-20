package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UforeHistorikk {

    private List<Uforeperiode> uforeperiodeListe = new ArrayList<>();

    public List<Uforeperiode> getUforeperiodeListe() {
        return uforeperiodeListe;
    }

    public void setUforeperiodeListe(List<Uforeperiode> uforeperiodeListe) {
        this.uforeperiodeListe = uforeperiodeListe;
    }
}
