package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AfpHistorikkDto {

    LocalDate virkFom;
    LocalDate virkTom;

    public LocalDate getVirkFom() {
        return virkFom;
    }

    public void setVirkFom(LocalDate virkFom) {
        this.virkFom = virkFom;
    }

    public LocalDate getVirkTom() {
        return virkTom;
    }

    public void setVirkTom(LocalDate virkTom) {
        this.virkTom = virkTom;
    }
}
