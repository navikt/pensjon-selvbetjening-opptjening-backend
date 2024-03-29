package no.nav.pensjon.selvbetjeningopptjening.person;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static no.nav.pensjon.selvbetjeningopptjening.person.PidIndexes.*;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.getDaysInMonth;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.isDayOfMonth;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.trimAllWhitespace;

public class PidValidator {

    private static final int FNR_LENGTH = 11;
    private static final int MONTHS_PER_YEAR = 12;
    private static final int DNR_DAG_ADDITION = 40;
    private static final int BOST_NUMMER_MAANED_ADDITION = 20; // BOST is a legacy PID type (replaced by NPID)
    private static final int DOLLY_FNR_MAANED_ADDITION = 40;
    private static final int NPID_SYNTHETIC_FNR_MAANED_ADDITION = 60;
    private static final int TESTNORGE_FNR_MAANED_ADDITION = 80;

    /**
     * "Special circumstances" means that the personnummer part (last 5 digits of FNR) does not follow the normal rules,
     * but has a special value like 00000 or 00001.
     */
    public static boolean isValidPid(String value, boolean acceptSpecialCircumstances) {
        if (value == null) {
            return false;
        }

        String trimmedValue = trimAllWhitespace(value);

        if (!isValid(trimmedValue, acceptSpecialCircumstances)) {
            return false;
        }

        String adjustedValue = makeDnrOrNpidOrSyntAdjustments(trimmedValue);
        return hasValidDatoPart(adjustedValue, isDnr(trimmedValue));
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

    private static String makeDnrOrNpidOrSyntAdjustments(String value) {
        if (!hasText(value)) {
            return value;
        }

        // FNR format will be <DDMMAAXXXYY>
        int dayValue = parseInt(value.substring(0, 2));
        int monthValue = parseInt(value.substring(2, 4));

        if (isMonth(monthValue, DOLLY_FNR_MAANED_ADDITION)) {
            value = replaceMonth(value, monthValue - DOLLY_FNR_MAANED_ADDITION);
        } else if (isMonth(monthValue, NPID_SYNTHETIC_FNR_MAANED_ADDITION)) {
            value = replaceMonth(value, monthValue - NPID_SYNTHETIC_FNR_MAANED_ADDITION);
        } else if (isMonth(monthValue, TESTNORGE_FNR_MAANED_ADDITION)) {
            value = replaceMonth(value, monthValue - TESTNORGE_FNR_MAANED_ADDITION);
        }

        if (isDnrDay(dayValue)) {
            return replaceDay(value, dayValue - DNR_DAG_ADDITION);
        } else if (isMonth(monthValue, BOST_NUMMER_MAANED_ADDITION)) {
            return replaceMonth(value, monthValue - BOST_NUMMER_MAANED_ADDITION);
        }

        // value is neither BOST-nr. nor D-nr.
        return value;
    }

    private static boolean isMonth(int value, int adjustment) {
        int monthValue = value - adjustment;
        return 1 <= monthValue && monthValue <= MONTHS_PER_YEAR;
    }

    private static String replaceDay(String value, int day) {
        return new StringBuffer(value).replace(0, 2, format("%02d", day)).toString();
    }

    private static String replaceMonth(String value, int month) {
        return new StringBuffer(value).replace(2, 4, format("%02d", month)).toString();
    }

    private static boolean isDnrDay(int day) {
        // In a D-nummer 40 is added to the date part
        return (day > DNR_DAG_ADDITION && day <= 71);
    }

    public static String getDatoPart(String pid) {
        String adjustedPid = makeDnrOrNpidOrSyntAdjustments(pid);
        return getDagAndMaaned(adjustedPid) + getAdjustedAar(adjustedPid, isDnr(pid));
    }

    /**
     * A D-nummer (DNR) is used as the PID for foreigners living in Norway.
     * In a DNR the number 4 has been added to the first digit in the PID;
     * otherwise it is similar to an FNR for native Norwegians.
     * Note that this method may not work on weakly validated PIDs (using special circumstances flag),
     * as such PIDs can never be guaranteed.
     */
    private static boolean isDnr(String value) {
        return isDnrDag(getDag(value));
    }

    private static String getDagAndMaaned(String pid) {
        return pid.substring(DAG_START, MAANED_END);
    }

    private static int getDag(String pid) {
        return parseInt(pid.substring(DAG_START, DAG_END));
    }

    private static int getMaaned(String pid) {
        return parseInt(pid.substring(MAANED_START, MAANED_END));
    }

    private static int getAar(String pid) {
        return parseInt(pid.substring(AAR_START, AAR_END));
    }

    private static int getIndividnummer(String pid) {
        return parseInt(pid.substring(INDIVIDNUMMER_START, INDIVIDNUMMER_END));
    }

    private static int getPersonnummer(String pid) {
        return parseInt(pid.substring(PERSONNUMMER_START));
    }

    private static int getAdjustedAar(String pid, boolean isDnr) {
        // Stillborn baby (dødfødt barn)
        if (!isDnr && getPersonnummer(pid) < 10) {
            return -1;
        }

        return getAdjustedAar(pid);
    }

    /**
     * For an explanation of the magic numbers used in this method, see
     * e.g. http://www.fnrinfo.no/Info/Oppbygging.aspx
     */
    private static int getAdjustedAar(String pid) {
        int individnummer = getIndividnummer(pid);
        int aar = getAar(pid);

        if (individnummer < 500) {
            return aar + 1900;
        }

        if (individnummer < 750 && 54 < aar) {
            return aar + 1800;
        }

        if (individnummer < 1000 && aar < 40) {
            return aar + 2000;
        }

        if (900 <= individnummer && individnummer < 1000) {
            return aar + 1900;
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
    private static boolean isSpecialCircumstance(String pid) {
        int personnummer = getPersonnummer(pid);
        return personnummer == 0 || personnummer == 1;
    }

    private static boolean isDnrDag(int value) {
        return isDayOfMonth(value - DNR_DAG_ADDITION);
    }

    private static boolean hasValidDatoPart(String pid, boolean isDnr) {
        boolean validDato = true;
        int maaned = getMaaned(pid);

        if (maaned > TESTNORGE_FNR_MAANED_ADDITION) {
            maaned -= TESTNORGE_FNR_MAANED_ADDITION;
        } else if (maaned > DOLLY_FNR_MAANED_ADDITION) {
            maaned -= DOLLY_FNR_MAANED_ADDITION;
        }

        int aar = getAdjustedAar(pid, isDnr);
        boolean isSpecial = isSpecialCircumstance(pid);

        if (aar == -1 && !isSpecial) {
            return false; // invalid year
        }

        int dag = getDag(pid);

        if (dag < 1) {
            validDato = false;
        }

        try {
            validDato &= (dag <= getDagerInMaaned(maaned, aar));
            return validDato;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static int getDagerInMaaned(int maaned, int aar) {
        if (maaned == 2 && aar == -1) {
            return 29; // For unknown reasons
        }

        return getDaysInMonth(maaned, aar);
    }
}
