package no.nav.pensjon.selvbetjeningopptjening.tjenestepensjon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tjenestepensjonsforhold {
    private String ordning;

    @JsonCreator
    public Tjenestepensjonsforhold(@JsonProperty("ordning") String ordning) {
        this.ordning = ordning;
    }

    public String getOrdning() {
        return ordning;
    }
}
