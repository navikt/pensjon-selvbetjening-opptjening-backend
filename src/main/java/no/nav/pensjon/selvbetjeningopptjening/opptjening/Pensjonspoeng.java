package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class Pensjonspoeng {

    private final boolean hasYear;
    private final boolean hasOmsorg;
    private final int year;
    private final String type;
    private final double poeng;
    private final Inntekt inntekt;
    private final Omsorg omsorg;

    public Pensjonspoeng(Integer year, String type, Double poeng, Inntekt inntekt, Omsorg omsorg) {
        this.year = year == null ? 1900 : year;
        this.hasYear = year != null;
        this.type = type;
        this.poeng = poeng == null ? 0D : poeng;
        this.inntekt = inntekt;
        this.omsorg = omsorg;
        this.hasOmsorg = omsorg != null;
    }

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public double getPoeng() {
        return poeng;
    }

    public Inntekt getInntekt() {
        return inntekt;
    }

    public boolean hasYear() {
        return hasYear;
    }

    public Omsorg getOmsorg() {
        return omsorg;
    }

    public boolean hasOmsorg() {
        return hasOmsorg;
    }
}
