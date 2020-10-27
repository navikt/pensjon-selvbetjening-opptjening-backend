package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;

public class DateUtil {

    public static boolean isDateInPeriod(LocalDate date, LocalDate periodFom, LocalDate periodTom) {
        return date.isAfter(periodFom) && (periodTom == null || date.isBefore(periodTom)) || date.isEqual(periodFom)
                || (periodTom != null && date.isEqual(periodTom));
    }
}
