package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Opptjening {

    private final String fnr;
    private final int inntekt;

    public Opptjening(String fnr, int inntekt) {
        this.fnr = fnr;
        this.inntekt = inntekt;
    }

    public String getFnr() {
        return fnr;
    }

    public int getInntekt() {
        return inntekt;
    }
}
