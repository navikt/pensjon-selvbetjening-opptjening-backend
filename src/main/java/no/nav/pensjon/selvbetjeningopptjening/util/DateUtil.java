package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;
import java.time.Month;

public class DateUtil {

    public static LocalDate firstDayOf(int year) {
        return LocalDate.of(year, Month.JANUARY, 1);
    }

    public static LocalDate reguleringDayOf(int year) {
        return LocalDate.of(year, Month.MAY, 1);
    }

    public static LocalDate lastDayOf(int year) {
        return LocalDate.of(year, Month.DECEMBER, 31);
    }

    public static boolean isDateInPeriod(LocalDate date, LocalDate periodFom, LocalDate periodTom) {
        return date.isAfter(periodFom) && (periodTom == null || date.isBefore(periodTom)) || date.isEqual(periodFom)
                || (periodTom != null && date.isEqual(periodTom));
    }
}
