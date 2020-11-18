package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Uforeopptjening {

    private int uforegrad;
    private double belop;

    public Uforeopptjening(Integer uforegrad, Double belop) {
        this.uforegrad = uforegrad == null ? 0 : uforegrad;
        this.belop = belop == null ? 0D : belop;
    }

    public double getBelop() {
        return belop;
    }

    public int getUforegrad() {
        return uforegrad;
    }
}
