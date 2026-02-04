package no.nav.pensjon.selvbetjeningopptjening.person

import com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.getDaysInMonth
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.isDayOfMonth
import org.springframework.util.StringUtils.hasText
import org.springframework.util.StringUtils.trimAllWhitespace
import java.lang.Integer.parseInt

object PidValidator {
    private const val FNR_LENGTH: Int = 11
    private const val MONTHS_PER_YEAR: Int = 12
    private const val MAX_DAGER_I_FEBRUAR: Int = 29 // TODO: merge with DAYS_IN_FEBRUARY_IN_LEAP_YEARS
    private const val DNR_DAG_ADDITION: Int = 40
    private const val BOST_NUMMER_MAANED_ADDITION: Int = 20 // BOST is a legacy PID type (replaced by NPID)
    private const val DOLLY_FNR_MAANED_ADDITION: Int = 40
    private const val NPID_SYNTHETIC_FNR_MAANED_ADDITION: Int = 60
    private const val TESTNORGE_FNR_MAANED_ADDITION: Int = 80
    private const val DOEDFOEDT_BARN_AAR: Int = -1

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

        return if (isValid(trimmedValue, acceptSpecialCircumstances).not())
            false
        else
            hasValidDatoPart(
                pid = makeDnrOrNpidOrSyntAdjustments(trimmedValue),
                isDnr = isDnr(trimmedValue)
            )
    }

    fun getDatoPart(pid: String): String {
        val adjustedPid = makeDnrOrNpidOrSyntAdjustments(pid)
        return rawDagAndMaaned(adjustedPid) + adjustedAar(adjustedPid, isDnr(pid))
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
        val maaned = rawMaaned(value)

        val result =
            when {
                isValidMaaned(value = maaned, adjustment = DOLLY_FNR_MAANED_ADDITION) ->
                    replaceMaaned(value, maaned - DOLLY_FNR_MAANED_ADDITION)

                isValidMaaned(value = maaned, adjustment = NPID_SYNTHETIC_FNR_MAANED_ADDITION) ->
                    replaceMaaned(value, maaned - NPID_SYNTHETIC_FNR_MAANED_ADDITION)

                isValidMaaned(value = maaned, adjustment = TESTNORGE_FNR_MAANED_ADDITION) ->
                    replaceMaaned(value, maaned - TESTNORGE_FNR_MAANED_ADDITION)

                else -> value
            }

        val dag: Int = dag(value)

        return when {
            isDnrDag(dag) -> replaceDag(result, dag - DNR_DAG_ADDITION)

            isValidMaaned(maaned, BOST_NUMMER_MAANED_ADDITION) ->
                replaceMaaned(result, maaned - BOST_NUMMER_MAANED_ADDITION)

            else -> result // value is neither BOST-nr. nor D-nr.
        }
    }

    private fun isValidMaaned(value: Int, adjustment: Int): Boolean {
        val monthValue = value - adjustment
        return monthValue in 1..MONTHS_PER_YEAR
    }

    private fun replaceDag(value: String, dag: Int): String =
        StringBuffer(value).replace(DAG_START, DAG_END, String.format("%02d", dag)).toString()

    private fun replaceMaaned(value: String, maaned: Int): String =
        StringBuffer(value).replace(MAANED_START, MAANED_END, String.format("%02d", maaned)).toString()

    private fun isDnrDag(value: Int): Boolean =
        isDayOfMonth(value - DNR_DAG_ADDITION)

    /**
     * A D-nummer (DNR) is used as the PID for foreigners living in Norway.
     * In a DNR the number 4 has been added to the first digit in the PID;
     * otherwise it is similar to an FNR for native Norwegians.
     * Note that this method may not work on weakly validated PIDs (using a 'special circumstances' flag),
     * as such PIDs can never be guaranteed.
     */
    private fun isDnr(value: String): Boolean =
        isDnrDag(dag(value))

    private fun dag(pid: String): Int =
        parseInt(pid.take(DAG_END))

    private fun rawDagAndMaaned(pid: String): String =
        pid.substring(DAG_START, MAANED_END)

    private fun rawMaaned(pid: String): Int =
        parseInt(pid.substring(MAANED_START, MAANED_END))

    private fun rawAar(pid: String): Int =
        parseInt(pid.substring(AAR_START, AAR_END))

    private fun individnummer(pid: String): Int =
        parseInt(pid.substring(INDIVIDNUMMER_START, INDIVIDNUMMER_END))

    private fun personnummer(pid: String): Int =
        parseInt(pid.substring(PERSONNUMMER_START))

    private fun adjustedMaaned(pid: String): Int =
        rawMaaned(pid).let { it - maanedAdjustment(maaned = it) }

    private fun adjustedAar(pid: String, isDnr: Boolean): Int =
        if (isDnr.not() && personnummer(pid) < 10)
            DOEDFOEDT_BARN_AAR
        else
            adjustedAar(pid)

    /**
     * For an explanation of the magic numbers used in this method, see
     * e.g. http://www.fnrinfo.no/Info/Oppbygging.aspx
     */
    private fun adjustedAar(pid: String): Int {
        val individnummer = individnummer(pid)
        val aar = rawAar(pid)

        return when {
            individnummer < 500 -> aar + 1900
            individnummer < 750 && 54 < aar -> aar + 1800
            individnummer < 1000 && aar < 40 -> aar + 2000
            individnummer in 900..<1000 -> aar + 1900
            else -> DOEDFOEDT_BARN_AAR
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

        // Try to satisfy overflow check in CodeQL:
        if (d1 + d2 + m1 + m2 + a1 + a2 + i1 + i2 + i3 + k1 + k2 !in 0..99) return false

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
        personnummer(pid).let { it == 0 || it == 1 }

    private fun hasValidDatoPart(pid: String, isDnr: Boolean): Boolean {
        val aar: Int = adjustedAar(pid, isDnr)

        if (aar == DOEDFOEDT_BARN_AAR && isSpecialCircumstance(pid).not()) {
            return false // invalid year
        }

        val dag = dag(pid)

        return try {
            0 < dag && dag <= dagerIMaaneden(maaned = adjustedMaaned(pid), aar)
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun maanedAdjustment(maaned: Int): Int =
        when {
            maaned > TESTNORGE_FNR_MAANED_ADDITION -> TESTNORGE_FNR_MAANED_ADDITION
            maaned > DOLLY_FNR_MAANED_ADDITION -> DOLLY_FNR_MAANED_ADDITION
            else -> 0
        }

    private fun dagerIMaaneden(maaned: Int, aar: Int): Int =
        if (maaned == 2 && aar == DOEDFOEDT_BARN_AAR)
            MAX_DAGER_I_FEBRUAR // år -1 anses her som skuddår
        else
            getDaysInMonth(maaned, aar)
}