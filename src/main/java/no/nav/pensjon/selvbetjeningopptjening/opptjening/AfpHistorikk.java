package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;

public class AfpHistorikk {

    private final LocalDate virkningFom;
    private final LocalDate virkningTom;

    public AfpHistorikk(LocalDate virkningFom, LocalDate virkningTom) {
        this.virkningFom = virkningFom;
        this.virkningTom = virkningTom;
    }

    public LocalDate getVirkningFom() {
        return virkningFom;
    }

    LocalDate getVirkningTom() {
        return virkningTom;
    }
}
