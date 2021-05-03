package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.time.LocalDate;

public class PdlFolkeregisterMetadata {
    private LocalDate ajourholdTidspunkt;

    public LocalDate getAjourholdTidspunkt() {
        return ajourholdTidspunkt;
    }

    public void setAjourholdTidspunkt(LocalDate ajourholdTidspunkt) {
        this.ajourholdTidspunkt = ajourholdTidspunkt;
    }
}
