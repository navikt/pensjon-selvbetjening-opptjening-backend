package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;

public class Pid {
    private String pid;

    public Pid(String pid) throws PidValidationException {
        this.pid = pid;
        validate(false);
    }

    public Pid(String pid, boolean acceptSpecialCircumstances) throws PidValidationException {
        this.pid = pid;
        validate(acceptSpecialCircumstances);
    }

    public String getPid() {
        return pid;
    }

    public LocalDate getFodselsdato() {
        // Adjust bnr or dnr (for fnr return value will be equal to pid)
        String adjustedFnr = makeDnrOrBostnrAdjustments(pid);

        String dateString = adjustedFnr.substring(0, 4) + get4DigitYearOfBirthWithAdjustedFnr(adjustedFnr, this.isDnummer());
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("ddMMyyyy"));
        } catch (DateTimeParseException e) {
            throw new PidValidationException("The supplied pid does not contain fodselsdato");
        }
    }

    private static int getDay(String validPid) {
        return Integer.parseInt(validPid.substring(0, 2));
    }

    private static int getMonth(String validPid) {
        return Integer.parseInt(validPid.substring(2, 4));
    }

    /**
     * Determines whether the specified string is a valid personal identification number. A valid PID can be: FNR, DNR or
     * BostNr. This method does not check for special circumstances i.e. where the personnummer has a specific value like 00000
     * or 000001.
     *
     * @param pid personal identification id to validate
     * @return <code>true</code> if the specified string is valid, otherwise <code>false</code>
     */
    public static boolean isValidPid(String pid) {
        return isValidPid(pid, false);
    }

    /**
     * Determines whether the specified string is a valid personal identification number. A valid PID can be: FNR, DNR or BostNr
     *
     * @param pid                        personal identification id to validate
     * @param acceptSpecialCircumstances flag indicating if the method should accept special circumstances. Special circumstances are where the
     *                                   personnummer does not follow the normal rules, but has a special value like 00000 or 00001
     * @return <code>true</code> if the specified string is valid, otherwise <code>false</code>
     */
    public static boolean isValidPid(String pid, boolean acceptSpecialCircumstances) {
        if (pid != null) {
            String value = StringUtils.deleteWhitespace(pid);
            if (isValidCharacters(value) && isValidFnrLength(value)) {
                boolean isValid;

                // non-strict validation
                if (acceptSpecialCircumstances) {
                    isValid = isMod11Compliant(value) || isSpecialCircumstance(value);

                    // strict validation
                } else if (isDnummer(value)) {
                    isValid = isMod11Compliant(value);
                } else {
                    isValid = isMod11Compliant(value) && !isSpecialCircumstance(value);
                }

                if (isValid) {
                    String fnr = makeDnrOrBostnrAdjustments(value);
                    return isFnrDateValid(fnr, isDnummer(value));
                }
            }
        }
        return false;
    }

    /**
     * Checks if the Pid value could represent a D-nummer. A D-nummer is used as the birthnumber for foreigners living in
     * Norway. In a D-nummer, the number 4 has been added to the first cipher in the Pid. Otherwise it is similar to a
     * birthnumber for native Norwegians.
     * Note that this method may not work on weakly validated Pids (using special circumstances flag), as such Pids can never be
     * guaranteed.
     *
     * @return <code>true</code> if Pid is representing a D-nummer, otherwise <code>false</code>
     */
    public boolean isDnummer() {
        return isDnummer(this.pid);
    }

    /**
     * Calculates wether the Pid parameter is representing a D-nummer.
     *
     * @param pidValue The Pid to check
     * @return true if it is a D-nummer
     */
    private static boolean isDnummer(String pidValue) {
        return isDnrDay(getDay(pidValue));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pid) {
            Pid pidObj = (Pid) obj;
            return pid.equals(pidObj.pid);
        } else {
            return false;
        }
    }

    /**
     * Validates that <code>this</code> is a valid Pid.
     *
     * @param acceptSpecialCircumstances flag indicating if the method should accept special circumstances. Special circumstances are where the
     *                                   personnummer (last 5 digit of fnr) does not follow the normal rules, but has a special value like 00000 or
     *                                   00001
     * @throws PidValidationException if validation of <code>this</code> fails
     */
    private void validate(boolean acceptSpecialCircumstances) throws PidValidationException {
        if (!isValidPid(pid, acceptSpecialCircumstances)) {
            throw new PidValidationException("The supplied pid is not a valid personal identification number");
        }
    }

    private static boolean isValidFnrLength(String fnr) {
        return fnr != null && (fnr.length() == 11);
    }

    private static boolean isValidCharacters(String fnr) {
        return StringUtils.isNumeric(fnr);
    }

    /**
     * Checks that a fnr is valid according to the modulus 11 control.
     *
     * @param fnr fodselsnummer
     * @return true if fnr is valid, otherwise false
     */
    private static boolean isMod11Compliant(String fnr) {
        // FORMAT: DDMMYYXXXYY
        int d1 = Integer.parseInt(fnr.substring(0, 1));
        int d2 = Integer.parseInt(fnr.substring(1, 2));
        int m1 = Integer.parseInt(fnr.substring(2, 3));
        int m2 = Integer.parseInt(fnr.substring(3, 4));
        int a1 = Integer.parseInt(fnr.substring(4, 5));
        int a2 = Integer.parseInt(fnr.substring(5, 6));
        int i1 = Integer.parseInt(fnr.substring(6, 7));
        int i2 = Integer.parseInt(fnr.substring(7, 8));
        int i3 = Integer.parseInt(fnr.substring(8, 9));
        int k1 = Integer.parseInt(fnr.substring(9, 10));
        int k2 = Integer.parseInt(fnr.substring(10));

        // control 1
        int v1 = (3 * d1) + (7 * d2) + (6 * m1) + (m2) + (8 * a1) + (9 * a2) + (4 * i1) + (5 * i2) + (2 * i3);

        int tmp = v1 / 11;
        int rest1 = v1 - (tmp * 11);
        int kontK1 = (rest1 == 0) ? 0 : (11 - rest1);

        // control 2
        int v2 = (5 * d1) + (4 * d2) + (3 * m1) + (2 * m2) + (7 * a1) + (6 * a2) + (5 * i1) + (4 * i2) + (3 * i3) + (2 * k1);
        tmp = v2 / 11;
        int rest2 = v2 - (tmp * 11);
        int kontK2 = (rest2 == 0) ? 0 : (11 - rest2);

        // checks that control number is correct
        return kontK1 == k1 && kontK2 == k2;
    }

    /**
     * Checks that a fnr is valid special circumstance. A special circumstance is when the personnummer is 0 or 1.
     *
     * @param fnr fodselsnummer
     * @return <code>true</code> if fnr is valid special circumstance, otherwise <code>false</code>
     */
    private static boolean isSpecialCircumstance(String fnr) {
        int val = Integer.parseInt(fnr.substring(6));

        return val == 0 || val == 1;
    }

    /**
     * Checks that a day may be a D-nummer.
     *
     * @param day Day part of the Pid
     * @return <code>true</code> if Day could be a D-number, otherwise <code>false</code>
     */
    private static boolean isDnrDay(int day) {
        // In a D-nummer 40 is added to the date part
        return (day > 40 && day <= 71);
    }

    /**
     * Validates that the first six digits of a fnr represents a valid birth date.
     *
     * @param dnrOrBnrAdjustedFnr - 11 digit f�dselsnummer, ajdusted if bnr or fnr
     * @param isDnummer           indicates if the dnrOrBnrAdjusteFnr is a Dnr
     * @return <code>true</code> if fnr can be converted to a valid date, otherwise <code>false</code>
     */
    private static boolean isFnrDateValid(String dnrOrBnrAdjustedFnr, boolean isDnummer) {
        boolean validDate = true;

        // fnr format is <DDMMAAXXXYY>
        int day = getDay(dnrOrBnrAdjustedFnr);
        int month = getMonth(dnrOrBnrAdjustedFnr);
        int year = get4DigitYearOfBirthWithAdjustedFnr(dnrOrBnrAdjustedFnr, isDnummer);

        boolean isSpecial = isSpecialCircumstance(dnrOrBnrAdjustedFnr);

        // invalid birth year
        if (year == -1 && !isSpecial) {
            return false;
        }

        if (day < 1) {
            validDate = false;
        }

        switch (month) {
            case 1: // january
            case 3: // march
            case 5: // may
            case 7: // july
            case 8: // august
            case 10: // october
            case 12: // december
                validDate &= (day <= 31);
                break;

            case 4: // april
            case 6: // june
            case 9: // september
            case 11: // november
                validDate &= (day <= 30);
                break;

            case 2: // february
                /*
                 * Leap year calculation:
                 *
                 * Rule: If year can be devided by 4, it's a leap year
                 *
                 * Exeception 1: If also can be divided by 100, it's NOT a leap year Exception 2: If year can be devided by 100 AND
                 * 400, it IS a leap year
                 */
                if (year == -1) {
                    validDate &= (day <= 29);
                } else {
                    if (year % 100 == 0 && year % 400 == 0) {
                        // leap year
                        validDate &= (day <= 29);
                    } else if (year % 100 != 0 && year % 4 == 0) {
                        // leap year
                        validDate &= (day <= 29);
                    } else {
                        // NOT leap year
                        validDate &= (day <= 28);
                    }
                }
                break;

            default: // invalid month
                validDate = false;
        }

        return validDate;
    }

    /**
     * Adjusts DNR and BostNr so that the first 6 numbers represents a valid date In the case where DNR or BostNr is the input,
     * the return value will fail a modulus 11 check.
     *
     * @param value a personal identification number
     * @return the inparam if it wasn't a DNR or BostNr, otherwise the BostNr/DNR where the 6 first digits can be converted to a
     * valid date
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

    private static int get4DigitYearOfBirthWithAdjustedFnr(String dnrOrBnrAdjustedFnr, boolean isDnummer) {
        int year = Integer.parseInt(dnrOrBnrAdjustedFnr.substring(4, 6));
        int individnr = Integer.parseInt(dnrOrBnrAdjustedFnr.substring(6, 9));
        // stilborn baby (dodfodt barn)
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
            } else if (900 <= individnr && individnr < 1000) {
                year += 1900;
            } else {
                // invalid fnr
                return -1;
            }
        }
        return year;
    }

    @Override
    public String toString() {
        return this.pid;
    }
}
