package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class FnrUtil {

    public static LocalDate getFodselsdatoForFnr(String fnr) {
        // Adjust bnr or dnr (for fnr return value will be equal to pid)
        String adjustedFnr = makeDnrOrBostnrAdjustments(fnr);

        String dateString = adjustedFnr.substring(0, 4) + get4DigitYearOfBirthWithAdjustedFnr(adjustedFnr, isDnummer(fnr));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Adjusts DNR and BOST-nr. so that the first 6 numbers represent a valid date.
     * In the case where DNR or BOST-nr. is the input,
     * the return value will fail a modulus-11 check.
     *
     * @param value a personal identification number
     * @return the input value if it wasn't a DNR or BOST-nr.,
     * otherwise the BOST-nr./DNR where the 6 first digits can be converted to a valid date
     */
    private static String makeDnrOrBostnrAdjustments(String value) {
        if (isBlank(value)) {
            return value;
        }

        // fnr format will be <DDMMAAXXXYY>
        int day = getDay(value);
        int month = getMonth(value);

        // DNR adjustment
        if (isDnrDay(day)) {
            day -= 40;
            var fnr = new StringBuilder(value);

            if (day < 10) {
                fnr.replace(0, 2, "0" + day);
            } else {
                fnr.replace(0, 2, Integer.toString(day));
            }

            return fnr.toString();
        }

        if (month > 20 && month <= 32) {
            // BOST adjustments
            month -= 20;
            var fnr = new StringBuilder(value);

            if (month < 10) {
                fnr.replace(2, 4, "0" + month);
            } else {
                fnr.replace(2, 4, Integer.toString(month));
            }

            return fnr.toString();
        }

        // value was neither bostnr nor dnr
        return value;
    }

    private static int get4DigitYearOfBirthWithAdjustedFnr(String dnrOrBnrAdjustedFnr, boolean isDnummer) {
        int year = parseInt(dnrOrBnrAdjustedFnr.substring(4, 6));
        int individnr = parseInt(dnrOrBnrAdjustedFnr.substring(6, 9));
        // stilborn baby (dødfødt barn)
        if (!isDnummer && parseInt(dnrOrBnrAdjustedFnr.substring(6)) < 10) {
            return -1;
        }

        // recalculating year
        if (individnr < 500) {
            year += 1900;
        } else if ((individnr < 750) && (54 < year)) {
            year += 1800;
        } else if ((individnr < 1000) && (year < 40)) {
            year += 2000;
        } else if (900 <= individnr && individnr < 1000) {
            year += 1900;
        } else {
            // invalid fnr
            return -1;
        }

        return year;
    }

    private static int getDay(String validPid) {
        return parseInt(validPid.substring(0, 2));
    }

    /**
     * Gets the month part of a PID (i.e. FNR, DNR or BOST-nr.)
     */
    private static int getMonth(String validPid) {
        return parseInt(validPid.substring(2, 4));
    }

    private static boolean isDnrDay(int day) {
        // In a D-nummer 40 is added to the date part
        return day > 40 && day <= 71;
    }

    private static boolean isDnummer(String pidValue) {
        return isDnrDay(getDay(pidValue));
    }
}
