package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UforeHistorikkDto {

    private List<UforeperiodeDto> uforeperiodeListe = new ArrayList<>();

    public List<UforeperiodeDto> getUforeperiodeListe() {
        return uforeperiodeListe;
    }

    public void setUforeperiodeListe(List<UforeperiodeDto> uforeperiodeListe) {
        this.uforeperiodeListe = uforeperiodeListe;
    }
}
