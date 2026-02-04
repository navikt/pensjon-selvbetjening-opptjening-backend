package no.nav.pensjon.selvbetjeningopptjening.person

import com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.getDaysInMonth
import org.springframework.util.StringUtils.hasText
import org.springframework.util.StringUtils.trimAllWhitespace
import java.lang.Integer.parseInt

object PidValidator {
    private const val FNR_LENGTH: Int = 11
    private const val MONTHS_PER_YEAR: Int = 12
    private const val DNR_DAG_ADDITION: Int = 40
    private const val BOST_NUMMER_MAANED_ADDITION: Int = 20 // BOST is a legacy PID type (replaced by NPID)
    private const val DOLLY_FNR_MAANED_ADDITION: Int = 40
    private const val NPID_SYNTHETIC_FNR_MAANED_ADDITION: Int = 60
    private const val TESTNORGE_FNR_MAANED_ADDITION: Int = 80
    // Indexes of parts of a person-ID:
    private const val DAG_START: Int = 0
    private const val MAANED_START: Int = 2
    private const val AAR_START: Int = 4
    private const val INDIVIDNUMMER_START: Int = 6
    private const val KONTROLLNUMMER_START: Int = 9
    private const val DAG_END: Int = MAANED_START
    private const val MAANED_END: Int = AAR_START
    private const val AAR_END: Int = INDIVIDNUMMER_START
    private const val INDIVIDNUMMER_END: Int = KONTROLLNUMMER_START
    private const val PERSONNUMMER_START: Int = INDIVIDNUMMER_START

    /**
     * "Special circumstances" means that the personnummer part (last 5 digits of FNR) does not follow the normal rules,
     * but has a special value like 00000 or 00001.
     */
    fun isValidPid(value: String?, acceptSpecialCircumstances: Boolean = false): Boolean {
        val trimmedValue = value?.let(::trimAllWhitespace) ?: return false

        if (isValid(trimmedValue, acceptSpecialCircumstances).not()) {
            return false
        }

        val adjustedValue = makeDnrOrNpidOrSyntAdjustments(trimmedValue)
        return hasValidDatoPart(pid = adjustedValue, isDnr = isDnr(trimmedValue))
    }

    fun getDatoPart(pid: String): String {
        val adjustedPid = makeDnrOrNpidOrSyntAdjustments(pid)
        return getDagAndMaaned(adjustedPid) + getAdjustedAar(adjustedPid, isDnr(pid))
    }

    private fun isValid(value: String, acceptSpecialCircumstances: Boolean): Boolean =
        hasValidCharacters(value)
                && hasValidFnrLength(value)
                && isModulus11Compliant(value, acceptSpecialCircumstances)

    private fun makeDnrOrNpidOrSyntAdjustments(value: String): String {
        if (hasText(value).not()) {
            return value
        }

        // FNR format will be <DDMMAAXXXYY>
        val monthValue = parseInt(value.substring(2, 4))

        val result =
            when {
                isMonth(monthValue, DOLLY_FNR_MAANED_ADDITION) ->
                    replaceMonth(value, monthValue - DOLLY_FNR_MAANED_ADDITION)

                isMonth(monthValue, NPID_SYNTHETIC_FNR_MAANED_ADDITION) ->
                    replaceMonth(value, monthValue - NPID_SYNTHETIC_FNR_MAANED_ADDITION)

                isMonth(monthValue, TESTNORGE_FNR_MAANED_ADDITION) ->
                    replaceMonth(value, monthValue - TESTNORGE_FNR_MAANED_ADDITION)

                else -> value
            }

        val dayValue = parseInt(value.take(2))

        return when {
            isDnrDay(dayValue) -> replaceDay(result, dayValue - DNR_DAG_ADDITION)

            isMonth(monthValue, BOST_NUMMER_MAANED_ADDITION) ->
                replaceMonth(result, monthValue - BOST_NUMMER_MAANED_ADDITION)

            else -> result // value is neither BOST-nr. nor D-nr.
        }
    }

    private fun isMonth(value: Int, adjustment: Int): Boolean {
        val monthValue = value - adjustment
        return monthValue in 1..MONTHS_PER_YEAR
    }

    private fun replaceDay(value: String, day: Int): String =
        StringBuffer(value).replace(0, 2, String.format("%02d", day)).toString()

    private fun replaceMonth(value: String, month: Int): String =
        StringBuffer(value).replace(2, 4, String.format("%02d", month)).toString()

    private fun isDnrDay(day: Int): Boolean {
        // In a D-nummer 40 is added to the date part
        return (day in DNR_DAG_ADDITION..71)
    }

    /**
     * A D-nummer (DNR) is used as the PID for foreigners living in Norway.
     * In a DNR the number 4 has been added to the first digit in the PID;
     * otherwise it is similar to an FNR for native Norwegians.
     * Note that this method may not work on weakly validated PIDs (using a 'special circumstances' flag),
     * as such PIDs can never be guaranteed.
     */
    private fun isDnr(value: String): Boolean =
        isDnrDag(getDag(value))

    private fun getDagAndMaaned(pid: String): String =
        pid.substring(DAG_START, MAANED_END)

    private fun getDag(pid: String): Int =
        parseInt(pid.substring(DAG_START, DAG_END))

    private fun getMaaned(pid: String): Int =
        parseInt(pid.substring(MAANED_START, MAANED_END))

    private fun getAar(pid: String): Int =
        parseInt(pid.substring(AAR_START, AAR_END))

    private fun getIndividnummer(pid: String): Int =
        parseInt(pid.substring(INDIVIDNUMMER_START, INDIVIDNUMMER_END))

    private fun getPersonnummer(pid: String): Int =
        parseInt(pid.substring(PERSONNUMMER_START))

    private fun getAdjustedAar(pid: String, isDnr: Boolean): Int =
        if (isDnr.not() && getPersonnummer(pid) < 10)
            -1 // Stillborn baby (dødfødt barn)
        else
            getAdjustedAar(pid)

    /**
     * For an explanation of the magic numbers used in this method, see
     * e.g. http://www.fnrinfo.no/Info/Oppbygging.aspx
     */
    private fun getAdjustedAar(pid: String): Int {
        val individnummer = getIndividnummer(pid)
        val aar = getAar(pid)

        return when {
            individnummer < 500 -> aar + 1900
            individnummer < 750 && 54 < aar -> aar + 1800
            individnummer < 1000 && aar < 40 -> aar + 2000
            individnummer in 900..<1000 -> aar + 1900
            else -> -1
        }
    }

    private fun isModulus11Compliant(value: String, acceptSpecialCircumstances: Boolean): Boolean =
        when {
            // non-strict validation
            acceptSpecialCircumstances -> isStrictlyModulus11Compliant(value) || isSpecialCircumstance(value)

            // strict validation
            isDnr(value) -> isStrictlyModulus11Compliant(value)

            else -> isStrictlyModulus11Compliant(value) && !isSpecialCircumstance(value)
        }

    private fun hasValidFnrLength(value: String?): Boolean =
        value?.length == FNR_LENGTH

    private fun hasValidCharacters(value: String?): Boolean =
        isNumeric(value)

    private fun isStrictlyModulus11Compliant(value: String): Boolean {
        // Format: DDMMYYiiikk
        val d1 = parseInt(value.take(1))
        val d2 = parseInt(value.substring(1, 2))
        val m1 = parseInt(value.substring(2, 3))
        val m2 = parseInt(value.substring(3, 4))
        val a1 = parseInt(value.substring(4, 5))
        val a2 = parseInt(value.substring(5, 6))
        val i1 = parseInt(value.substring(6, 7))
        val i2 = parseInt(value.substring(7, 8))
        val i3 = parseInt(value.substring(8, 9))
        val k1 = parseInt(value.substring(9, 10))
        val k2 = parseInt(value.substring(10))

        // Control 1:
        val v1 = 3 * d1 + 7 * d2 + 6 * m1 + m2 + 8 * a1 + 9 * a2 + 4 * i1 + 5 * i2 + 2 * i3
        var tmp = v1 / 11
        val rest1 = v1 - tmp * 11
        val kontK1 = if (rest1 == 0) 0 else 11 - rest1

        // Control 2:
        val v2 = 5 * d1 + 4 * d2 + 3 * m1 + 2 * m2 + 7 * a1 + 6 * a2 + 5 * i1 + 4 * i2 + 3 * i3 + 2 * k1
        tmp = v2 / 11
        val rest2 = v2 - tmp * 11
        val kontK2 = if (rest2 == 0) 0 else 11 - rest2

        // Check that control numbers are correct:
        return kontK1 == k1 && kontK2 == k2
    }

    /**
     * Checks that an FNR is formatted according to "special circumstances", i.e. when the personnummer part is 0 or 1.
     */
    private fun isSpecialCircumstance(pid: String): Boolean =
        getPersonnummer(pid).let { it == 0 || it == 1 }

    private fun isDnrDag(value: Int): Boolean =
        DateUtil.isDayOfMonth(value - DNR_DAG_ADDITION)

    private fun hasValidDatoPart(pid: String, isDnr: Boolean): Boolean {
        var validDato = true
        var maaned = getMaaned(pid)

        if (maaned > TESTNORGE_FNR_MAANED_ADDITION) {
            maaned -= TESTNORGE_FNR_MAANED_ADDITION
        } else if (maaned > DOLLY_FNR_MAANED_ADDITION) {
            maaned -= DOLLY_FNR_MAANED_ADDITION
        }

        val aar = getAdjustedAar(pid, isDnr)
        val isSpecial = isSpecialCircumstance(pid)

        if (aar == -1 && !isSpecial) {
            return false // invalid year
        }

        val dag = getDag(pid)

        if (dag < 1) {
            validDato = false
        }

        return try {
            validDato and (dag <= getDagerInMaaned(maaned, aar))
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun getDagerInMaaned(maaned: Int, aar: Int): Int =
        if (maaned == 2 && aar == -1)
            29 // For unknown reasons
        else
            getDaysInMonth(maaned, aar)
}