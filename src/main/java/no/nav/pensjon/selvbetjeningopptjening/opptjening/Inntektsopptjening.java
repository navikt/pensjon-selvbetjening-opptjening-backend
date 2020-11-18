package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Inntektsopptjening {

    private double belop;

    Inntektsopptjening(Double belop) {
        this.belop = belop == null ? 0D : belop;
    }

    public double getBelop() {
        return belop;
    }
}
