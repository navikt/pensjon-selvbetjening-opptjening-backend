package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;

public final class DateUtil {

    private static final String TIME_ZONE = "UTC+2"; // Norway daylight saving time
    private static final int MAX_DAY_OF_MONTH = 31;
    private static final int DAYS_IN_FEBRUARY_IN_NON_LEAP_YEARS = 28;
    private static final int DAYS_IN_FEBRUARY_IN_LEAP_YEARS = 29;
    private static final int MONTHS_PER_YEAR = 12;
    private static final int BASIC_LEAP_YEAR_INTERVAL = 4;
    private static final int EXCEPTION_LEAP_YEAR_INTERVAL = 100;
    private static final int EXCEPTION_TO_EXCEPTION_LEAP_YEAR_INTERVAL = 400;

    public static final ZoneId ZONE_ID = ZoneId.of(TIME_ZONE);

    public static LocalDate asLocalDate(Calendar calendar) {
        return calendar == null ? null : LocalDate.ofInstant(calendar.toInstant(), ZONE_ID);
    }

    public static LocalDate firstDayOf(int year) {
        return LocalDate.of(year, Month.JANUARY, 1);
    }

    public static LocalDate reguleringDayOf(int year) {
        return LocalDate.of(year, Month.MAY, 1);
    }

    public static LocalDate lastDayOf(int year) {
        return LocalDate.of(year, Month.DECEMBER, 31);
    }

    public static boolean isDateInPeriod(LocalDate date, LocalDate fom, LocalDate tom) {
        return date.isAfter(fom) && (tom == null || date.isBefore(tom)) || date.isEqual(fom)
                || (tom != null && date.isEqual(tom));
    }

    public static int getDaysInFebruary(int year) {
        return isLeapYear(year) ? DAYS_IN_FEBRUARY_IN_LEAP_YEARS : DAYS_IN_FEBRUARY_IN_NON_LEAP_YEARS;
    }

    private static boolean isLeapYear(int year) {
        return year % BASIC_LEAP_YEAR_INTERVAL == 0 && year % EXCEPTION_LEAP_YEAR_INTERVAL != 0 ||
                year % EXCEPTION_LEAP_YEAR_INTERVAL == 0 && year % EXCEPTION_TO_EXCEPTION_LEAP_YEAR_INTERVAL == 0;
    }

    public static boolean isDayOfMonth(int value) {
        return 1 <= value && value <= MAX_DAY_OF_MONTH;
    }

    public static boolean isMonth(int value) {
        return 1 <= value && value <= MONTHS_PER_YEAR;
    }

    public static int getDaysInMonth(int month, int year) {
        int daysInMonth;

        switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> daysInMonth = 31; // 1 = January
            case 4, 6, 9, 11 -> daysInMonth = 30;
            case 2 -> daysInMonth = getDaysInFebruary(year);
            default -> throw new IllegalArgumentException("Invalid month value: " + month);
        }

        return daysInMonth;
    }
}
