package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Inntektsopptjening {

    private final int year;
    private final double belop;
    private final Inntekt sumPensjonsgivendeInntekt;

    public Inntektsopptjening(Integer year, Double belop, Inntekt sumPensjonsgivendeInntekt) {
        this.year = year == null ? 1900 : year;
        this.belop = belop == null ? 0D : belop;
        this.sumPensjonsgivendeInntekt = sumPensjonsgivendeInntekt;
    }

    public double getBelop() {
        return belop;
    }

    Inntekt getSumPensjonsgivendeInntekt() {
        return sumPensjonsgivendeInntekt;
    }

    public int getYear() {
        return year;
    }
}
