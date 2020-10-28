package no.nav.pensjon.selvbetjeningopptjening.util;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Periode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodeUtil {

    public static <T extends Periode> List<T> sortPerioderByFomDate(List<T> unsortedPerioder) {
        List<T> copy = new ArrayList<>(unsortedPerioder);

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


    public static boolean isPeriodeWithinInterval(Periode periode, LocalDate start, LocalDate end) {
        LocalDate fom = periode.getFomDato();
        LocalDate tom = periode.getTomDato();

        return fom.isAfter(start)
                && (tom == null && (end == null || end.isAfter(fom))
                || (tom != null && (end == null || tom.isBefore(end))));
    }
}
