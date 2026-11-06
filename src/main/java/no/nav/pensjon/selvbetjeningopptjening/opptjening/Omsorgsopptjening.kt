package no.nav.pensjon.selvbetjeningopptjening.opptjening;


import java.util.ArrayList;
import java.util.List;

public class Omsorgsopptjening {

    private int year;
    private double belop;
    private List<Omsorg> omsorger;

    public Omsorgsopptjening(Integer year, Double belop, List<Omsorg> omsorger) {
        this.year = year == null ? 1900 : year;
        this.belop = belop == null ? 0D : belop;
        this.omsorger = omsorger == null ? new ArrayList<>() : omsorger;
    }

    public int getYear() {
        return year;
    }

    public double getBelop() {
        return belop;
    }

    public List<Omsorg> getOmsorger() {
        return omsorger;
    }
}
