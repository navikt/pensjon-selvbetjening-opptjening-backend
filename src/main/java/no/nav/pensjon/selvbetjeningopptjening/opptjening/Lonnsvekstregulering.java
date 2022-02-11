package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;

public class Lonnsvekstregulering {

    private final boolean hasBelop;
    private final double belop;
    private final LocalDate reguleringsDato;

    public Lonnsvekstregulering(Double belop, LocalDate reguleringsDato) {
        this.belop = belop == null ? 0D : belop;
        this.hasBelop = belop != null;
        this.reguleringsDato = reguleringsDato;
    }

    public double getBelop() {
        return belop;
    }

    boolean hasBelop() {
        return hasBelop;
    }

    public LocalDate getReguleringsDato() {
        return reguleringsDato;
    }
}
