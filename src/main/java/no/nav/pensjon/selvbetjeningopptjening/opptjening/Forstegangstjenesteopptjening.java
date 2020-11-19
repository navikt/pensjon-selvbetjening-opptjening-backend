package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Forstegangstjenesteopptjening {

    private int year;
    private double belop;

    public Forstegangstjenesteopptjening(Integer year, Double belop) {
        this.year = year == null ? 1900 : year;
        this.belop = belop == null ? 0D : belop;
    }

    public double getBelop() {
        return belop;
    }

    public int getYear() {
        return year;
    }
}
