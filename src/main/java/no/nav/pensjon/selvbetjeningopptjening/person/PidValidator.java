package no.nav.pensjon.selvbetjeningopptjening.person;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.*;

public class PidValidator {

    private static final int FNR_LENGTH = 11;
    private static final int MAX_DAY_OF_MONTH = 31;
    private static final int MONTHS_PER_YEAR = 12;
    private static final int BNR_MONTH_ADDITION = 20;
    private static final int DNR_DAY_ADDITION = 40;
    private static final int TESTNORGE_FNR_MONTH_ADDITION = 80;

    /**
     * "Special circumstances" means that the personnummer part (last 5 digits of FNR) does not follow the normal rules,
     * but has a special value like 00000 or 00001.
     */
    public static boolean isValidPid(String value, boolean acceptSpecialCircumstances) {
        if (value == null) {
            return false;
        }

        String trimmedValue = deleteWhitespace(value);

        if (!isValid(trimmedValue, acceptSpecialCircumstances)) {
            return false;
        }

        String adjustedValue = makeDnrOrBnrAdjustments(trimmedValue);
        return hasValidDatePart(adjustedValue, isDnr(trimmedValue));
    }

    /**
     * This method does not check for special circumstances.
     */
    public static boolean isValidPid(String value) {
        return isValidPid(value, false);
    }

    private static boolean isValid(String value, boolean acceptSpecialCircumstances) {
        return hasValidCharacters(value)
                && hasValidFnrLength(value)
                && isModulus11Compliant(value, acceptSpecialCircumstances);
    }

    /**
     * If the input is a DNR (D-nummer) or BNR (BOST-nummer), it will be adjusted so that the first 6 numbers
     * represent a valid date (in this case the return value will fail a modulus 11 check).
     * If the input is neither a DNR nor a BNR (but e.g. an FNR), the value will be returned unchanged.
     */
    private static String makeDnrOrBnrAdjustments(String value) {
        if (isBlank(value)) {
            return value;
        }

        // D-nummer adjustment:
        int day = getDay(value) - DNR_DAY_ADDITION;

        if (isDayOfMonth(day)) {
            return new StringBuilder(value).replace(0, 2, as2Chars(day)).toString();
        }

        // BOST-nummer adjustment:
        int month = getMonth(value) - BNR_MONTH_ADDITION;

        if (isMonth(month)) {
            return new StringBuilder(value).replace(2, 4, as2Chars(month)).toString();
        }

        // value is neither BOST- nor D-nummer
        return value;
    }

    public static String getDatePart(String pid) {
        String adjustedPid = makeDnrOrBnrAdjustments(pid);
        return getDayAndMonth(adjustedPid) + getYear(adjustedPid, isDnr(pid));
    }

    /**
     * A D-nummer (DNR) is used as the PID for foreigners living in Norway.
     * In a DNR the number 4 has been added to the first digit in the PID;
     * otherwise it is similar to an FNR for native Norwegians.
     * Note that this method may not work on weakly validated PIDs (using special circumstances flag),
     * as such PIDs can never be guaranteed.
     */
    private static boolean isDnr(String value) {
        return isDnrDay(getDay(value));
    }

    private static String getDayAndMonth(String pid) {
        return pid.substring(0, 4);
    }

    private static int getDay(String pid) {
        return parseInt(pid.substring(0, 2));
    }

    private static int getMonth(String pid) {
        return parseInt(pid.substring(2, 4));
    }

    private static int getYear(String pid, boolean isDnr) {
        // Stillborn baby (dødfødt barn)
        if (!isDnr && parseInt(pid.substring(6)) < 10) {
            return -1;
        }

        return getYear(pid);
    }

    private static int getYear(String pid) {
        int individnummer = parseInt(pid.substring(6, 9));
        int year = parseInt(pid.substring(4, 6));

        if (individnummer < 500) {
            return year + 1900;
        }

        if (individnummer < 750 && 54 < year) {
            return year + 1800;
        }

        if (individnummer < 1000 && year < 40) {
            return year + 2000;
        }

        if (900 <= individnummer && individnummer < 1000) {
            return year + 1900;
        }

        return -1;
    }

    private static boolean isModulus11Compliant(String value, boolean acceptSpecialCircumstances) {
        // non-strict validation
        if (acceptSpecialCircumstances) {
            return isStrictlyModulus11Compliant(value) || isSpecialCircumstance(value);
        }

        // strict validation
        if (isDnr(value)) {
            return isStrictlyModulus11Compliant(value);
        }

        return isStrictlyModulus11Compliant(value) && !isSpecialCircumstance(value);
    }

    private static boolean hasValidFnrLength(String value) {
        return value != null && value.length() == FNR_LENGTH;
    }

    private static boolean hasValidCharacters(String value) {
        return isNumeric(value);
    }

    private static boolean isStrictlyModulus11Compliant(String value) {
        // Format: DDMMYYiiikk
        int d1 = parseInt(value.substring(0, 1));
        int d2 = parseInt(value.substring(1, 2));
        int m1 = parseInt(value.substring(2, 3));
        int m2 = parseInt(value.substring(3, 4));
        int a1 = parseInt(value.substring(4, 5));
        int a2 = parseInt(value.substring(5, 6));
        int i1 = parseInt(value.substring(6, 7));
        int i2 = parseInt(value.substring(7, 8));
        int i3 = parseInt(value.substring(8, 9));
        int k1 = parseInt(value.substring(9, 10));
        int k2 = parseInt(value.substring(10));

        // Control 1:
        int v1 = 3 * d1 + 7 * d2 + 6 * m1 + m2 + 8 * a1 + 9 * a2 + 4 * i1 + 5 * i2 + 2 * i3;
        int tmp = v1 / 11;
        int rest1 = v1 - tmp * 11;
        int kontK1 = rest1 == 0 ? 0 : 11 - rest1;

        // Control 2:
        int v2 = 5 * d1 + 4 * d2 + 3 * m1 + 2 * m2 + 7 * a1 + 6 * a2 + 5 * i1 + 4 * i2 + 3 * i3 + 2 * k1;
        tmp = v2 / 11;
        int rest2 = v2 - tmp * 11;
        int kontK2 = rest2 == 0 ? 0 : 11 - rest2;

        // Check that control numbers are correct:
        return kontK1 == k1 && kontK2 == k2;
    }

    /**
     * Checks that an FNR is formatted according to "special circumstances", i.e. when the personnummer part is 0 or 1.
     */
    private static boolean isSpecialCircumstance(String value) {
        int personnummer = parseInt(value.substring(6));
        return personnummer == 0 || personnummer == 1;
    }

    private static boolean isDnrDay(int value) {
        int normalDayOfMonth = value - DNR_DAY_ADDITION;
        return 1 <= normalDayOfMonth && normalDayOfMonth <= MAX_DAY_OF_MONTH;
    }

    private static boolean hasValidDatePart(String pid, boolean isDnr) {
        boolean validDate = true;
        int month = getMonth(pid);

        if (month > TESTNORGE_FNR_MONTH_ADDITION) {
            month -= TESTNORGE_FNR_MONTH_ADDITION;
        }

        int year = getYear(pid, isDnr);
        boolean isSpecial = isSpecialCircumstance(pid);

        if (year == -1 && !isSpecial) {
            return false; // invalid year
        }

        int day = getDay(pid);

        if (day < 1) {
            validDate = false;
        }

        switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> validDate &= (day <= MAX_DAY_OF_MONTH); // 1 = January
            case 4, 6, 9, 11 -> validDate &= (day <= 30);
            case 2 -> validDate &= (day <= daysInFebruary(year));
            default -> validDate = false;
        }

        return validDate;
    }

    private static int daysInFebruary(int year) {
        if (year == -1) {
            return 29; // For unknown reasons
        }

        return isLeapYear(year) ? 29 : 28;
    }

    private static boolean isLeapYear(int year) {
        return year % 100 == 0 && year % 400 == 0 ||
                year % 100 != 0 && year % 4 == 0;
    }

    private static boolean isDayOfMonth(int value) {
        return 1 <= value && value <= MAX_DAY_OF_MONTH;
    }

    private static boolean isMonth(int value) {
        return 1 <= value && value <= MONTHS_PER_YEAR;
    }

    private static String as2Chars(int value) {
        return value < 10 ? "0" + value : Integer.toString(value);
    }
}
