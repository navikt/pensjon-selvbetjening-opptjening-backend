package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.time.LocalDate;

public class PdlFolkeregisterMetadata {
    private LocalDate ajourholdstidspunkt;

    public LocalDate getAjourholdstidspunkt() {
        return ajourholdstidspunkt;
    }

    public void setAjourholdstidspunkt(LocalDate ajourholdstidspunkt) {
        this.ajourholdstidspunkt = ajourholdstidspunkt;
    }
}
