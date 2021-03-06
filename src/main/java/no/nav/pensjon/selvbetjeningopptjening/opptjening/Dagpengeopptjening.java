package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Dagpengeopptjening {

    private int year;
    private double ordinartBelop;
    private double fiskerBelop;

    public Dagpengeopptjening(Integer year, Double ordinartBelop, Double fiskerBelop) {
        this.year = year == null ? 1900 : year;
        this.ordinartBelop = ordinartBelop == null ? 0D : ordinartBelop;
        this.fiskerBelop = fiskerBelop == null ? 0D : fiskerBelop;
    }

    public int getYear() {
        return year;
    }

    public double getOrdinartBelop() {
        return ordinartBelop;
    }

    public double getFiskerBelop() {
        return fiskerBelop;
    }

    boolean hasPositiveBelop() {
        return ordinartBelop > 0 || fiskerBelop > 0;
    }
}
