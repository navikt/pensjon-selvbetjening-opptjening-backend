package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.time.LocalDate;

public class PdlFolkeregisterMetadata {
    private LocalDate ajourholdsTidspunkt;

    public LocalDate getAjourholdsTidspunkt() {
        return ajourholdsTidspunkt;
    }

    public void setAjourholdsTidspunkt(LocalDate ajourholdsTidspunkt) {
        this.ajourholdsTidspunkt = ajourholdsTidspunkt;
    }
}
