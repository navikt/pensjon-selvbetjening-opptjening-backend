package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.time.LocalDate;

public class PdlMetadataEndring {
    private LocalDate registrert;

    public LocalDate getRegistrert() {
        return registrert;
    }

    public void setRegistrert(LocalDate registrert) {
        this.registrert = registrert;
    }
}
