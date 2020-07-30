package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

public class FnrUtil {

    public static LocalDate getFodselsdatoForFnr(String fnr){
        // Adjust bnr or dnr (for fnr return value will be equal to pid)
        String adjustedFnr = makeDnrOrBostnrAdjustments(fnr);
        // Construct a date string with MMDDyyyy format

        String dateString = adjustedFnr.substring(0, 4) + get4DigitYearOfBirthWithAdjustedFnr(adjustedFnr, isDnummer(fnr));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Adjusts DNR and BostNr so that the first 6 numbers represents a valid date In the case where DNR or BostNr is the input,
     * the return value will fail a modulus 11 check.
     *
     * @param value
     *            a personal identification number
     * @return the inparam if it wasn't a DNR or BostNr, otherwise the BostNr/DNR where the 6 first digits can be converted to a
     *         valid date
     */
    private static String makeDnrOrBostnrAdjustments(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // fnr format will be <DDMMAAXXXYY>
        int day = getDay(value);
        int month = getMonth(value);

        // DNR adjustment
        if (isDnrDay(day)) {
            day -= 40;
            StringBuilder fnr = new StringBuilder(value);

            if (day < 10) {
                fnr.replace(0, 2, "0" + day);
            } else {
                fnr.replace(0, 2, Integer.toString(day));
            }

            return fnr.toString();
        } else if (month > 20 && month <= 32) {
            // BOST adjustments
            month -= 20;
            StringBuilder fnr = new StringBuilder(value);

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

    /**
     * Returns a 4-digit birth date.
     *
     * @param dnrOrBnrAdjustedFnr
     *            a fnr, adjusted if it's a bnr or dnr
     * @param isDnummer
     *            boolean that says wether the dnrOrBnrAdjustedFnr is a Dnr
     * @return 4 digit birth date, -1 if invalid
     */
    private static int get4DigitYearOfBirthWithAdjustedFnr(String dnrOrBnrAdjustedFnr, boolean isDnummer) {
        int year = Integer.parseInt(dnrOrBnrAdjustedFnr.substring(4, 6));
        int individnr = Integer.parseInt(dnrOrBnrAdjustedFnr.substring(6, 9));
        // stilborn baby (dødfødt barn)
        if (!isDnummer && Integer.parseInt(dnrOrBnrAdjustedFnr.substring(6)) < 10) {
            return -1;
        } else {
            // recalculating year
            if (individnr < 500) {
                year += 1900;
            } else if ((individnr < 750) && (54 < year)) {
                year += 1800;
            } else if ((individnr < 1000) && (year < 40)) {
                year += 2000;
            } else if ((900 <= individnr) && (individnr < 1000) && (39 < year)) {
                year += 1900;
            } else {
                // invalid fnr
                return -1;
            }
        }
        return year;
    }

    /**
     * Gets the day part of a valid Pid.
     *
     * @param validPid
     *            - a valid fnr, dnr or bostnr
     * @return Day in birth date part of Pid
     */
    private static int getDay(String validPid) {
        return Integer.parseInt(validPid.substring(0, 2));
    }

    /**
     * Gets the Month part of a valid Pid.
     *
     * @param validPid
     *            - a valid fnr, dnr or bostnr
     * @return Month in birth date part of Pid
     */
    private static int getMonth(String validPid) {
        return Integer.parseInt(validPid.substring(2, 4));
    }

    /**
     * Checks that a day may be a D-nummer.
     *
     * @param day
     *            Day part of the Pid
     * @return <code>true</code> if Day could be a D-number, otherwise <code>false</code>
     */
    private static boolean isDnrDay(int day) {
        // In a D-nummer 40 is added to the date part
        return (day > 40 && day <= 71);
    }

    /**
     * Calculates wether the Pid parameter is representing a D-nummer.
     *
     * @param pidValue
     *            The Pid to check
     * @return true if it is a D-nummer
     */
    private static boolean isDnummer(String pidValue) {
        return isDnrDay(getDay(pidValue));
    }
}
