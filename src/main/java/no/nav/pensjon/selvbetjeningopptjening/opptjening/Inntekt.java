package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Inntekt {

    private int year;
    private final String type;
    private long belop;

    public Inntekt(Integer year, String type, Long belop) {
        this.year = year == null ? 1900 : year;
        this.type = type;
        this.belop = belop == null ? 0L : belop;
    }

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public long getBelop() {
        return belop;
    }
}
