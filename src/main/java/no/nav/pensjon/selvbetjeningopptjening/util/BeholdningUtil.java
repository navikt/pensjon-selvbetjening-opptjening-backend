package no.nav.pensjon.selvbetjeningopptjening.util;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BeholdningUtil {

    public static List<Beholdning> sortBeholdningerByDate(List<Beholdning> unsortedBeholdninger) {
        List<Beholdning> copy = new ArrayList<>(unsortedBeholdninger);

        if (copy.size() <= 1) {
            return copy;
        }

        copy.sort((b1, b2) -> {
            LocalDate date1 = b1.getFomDato();
            LocalDate date2 = b2.getFomDato();
            return date1.compareTo(date2);
        });

        return copy;
    }

    public static boolean isBeholdningWithinPeriode(Beholdning beholdning, LocalDate start, LocalDate end) {
        LocalDate fom = beholdning.getFomDato();
        LocalDate tom = beholdning.getTomDato();

        return fom.isAfter(start)
                && (tom == null && (end == null || end.isAfter(fom))
                || (tom != null && (end == null || tom.isBefore(end))));
    }
}
