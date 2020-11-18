package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Lonnsvekstregulering {

    private final boolean hasBelop;
    private final double belop;

    public Lonnsvekstregulering(Double belop) {
        this.belop = belop == null ? 0D : belop;
        this.hasBelop = belop != null;
    }

    public double getBelop() {
        return belop;
    }

    boolean hasBelop() {
        return hasBelop;
    }
}
