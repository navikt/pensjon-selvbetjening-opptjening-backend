package no.nav.pensjon.selvbetjeningopptjening.person.pid

import no.nav.pensjon.selvbetjeningopptjening.common.Aarstall
import no.nav.pensjon.selvbetjeningopptjening.common.Base10Digit
import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.getDaysInMonth
import java.lang.Integer.parseInt

/**
 * Strukturert person-ID for behandling av dens bestanddeler.
 */
@ConsistentCopyVisibility
data class StructuredPid private constructor(
    val aar: PidAar,
    val maaned: PidMaaned,
    val dag: PidDag,
    val individnummer: Int,
    val personnummer: Int,
    val doedfoedt: Boolean,
    val digits: List<PidDigit>
) {
    constructor(pid: String) : this(
        aar = PidAar(pid.substring(AAR_START, AAR_END).toUByte()),
        maaned = PidMaaned(pid.substring(MAANED_START, MAANED_END).toUByte()),
        dag = PidDag.from(pid),
        individnummer = parseInt(pid.substring(INDIVIDNUMMER_START, INDIVIDNUMMER_END)),
        personnummer = personnummer(pid),
        doedfoedt = PidDag.from(pid).isDnrDag().not() && personnummer(pid) < 10,
        digits = IntRange(0, 10).map {
            PidDigit(
                base10Digit = Base10Digit(pid.substring(it, it + 1).toUByte()),
                weight1 = Base10Digit(weights1[it].toUByte()),
                weight2 = Base10Digit(weights2[it].toUByte())
            )
        }
    )

    fun isValid(acceptSpecialCircumstances: Boolean): Boolean =
        isModulus11Compliant(acceptSpecialCircumstances) &&
                this.medJustertDagOgMaaned().harGyldigDato()

    fun justertDato(): String =
        this.medJustertDagOgMaaned().datoPart()

    private fun medJustertDagOgMaaned(): StructuredPid =
        this.medMaaned(maaned.justert())
            .medDag(dag.justert())

    private fun datoPart(): String =
        String.format("%02d", dag.value.toInt()) +
                String.format("%02d", maaned.value.toInt()) +
                (if (doedfoedt) DOEDFOEDT_BARN_AAR else aar.justert(individnummer).value)

    private fun harGyldigDato(): Boolean {
        if (doedfoedt && isSpecialCircumstance().not()) {
            return false // invalid year
        }

        return try {
            dag.within(
                min = 1,
                max = dagerIMaaneden(maaned = maaned.justert(), aar = aar.justert(individnummer), doedfoedt)
            )
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun isModulus11Compliant(acceptSpecialCircumstances: Boolean): Boolean =
        when {
            // non-strict validation
            acceptSpecialCircumstances -> isStrictlyModulus11Compliant() || isSpecialCircumstance()

            // strict validation
            dag.isDnrDag() -> isStrictlyModulus11Compliant()

            else -> isStrictlyModulus11Compliant() && isSpecialCircumstance().not()
        }

    private fun isStrictlyModulus11Compliant(): Boolean {
        // Control 1:
        val v1: UShort = digits.sumOf { it.weighted1().toUInt() }.toUShort()
        val rest1: UByte = (v1 % MODULUS).toUByte()
        val kontK1: UByte = if (rest1 == ZERO) ZERO else (MODULUS - rest1).toUByte()

        // Control 2:
        val v2: UShort = digits.sumOf { it.weighted2().toUInt() }.toUShort()
        val rest2: UByte = (v2 % MODULUS).toUByte()
        val kontK2: UByte = if (rest2 == ZERO) ZERO else (MODULUS - rest2).toUByte()

        // Check that control numbers are correct:
        return kontK1 == digits[9].base10Digit.value && kontK2 == digits[10].base10Digit.value
    }

    /**
     * Checks that an FNR is formatted according to "special circumstances", i.e. when the personnummer part is 0 or 1.
     */
    private fun isSpecialCircumstance(): Boolean =
        personnummer.let { it == 0 || it == 1 }

    private fun medDag(value: PidDag) =
        StructuredPid(
            aar = aar,
            maaned = maaned,
            dag = value,
            individnummer = individnummer,
            personnummer = personnummer,
            doedfoedt = doedfoedt,
            digits = digits
        )

    private fun medMaaned(value: PidMaaned) =
        StructuredPid(
            aar = aar,
            maaned = value,
            dag = dag,
            individnummer = individnummer,
            personnummer = personnummer,
            doedfoedt = doedfoedt,
            digits = digits
        )

    private object Februar {
        const val MAANEDSNUMMER: Int = 2
        const val MAX_ANTALL_DAGER: Int = 29 // TODO: merge with DAYS_IN_FEBRUARY_IN_LEAP_YEARS
    }

    private companion object {
        private const val MODULUS: UByte = 11u
        private const val ZERO: UByte = 0u
        private const val DOEDFOEDT_BARN_AAR: Int = -1

        // Indexes of parts of a person-ID:
        private const val MAANED_START: Int = 2
        private const val AAR_START: Int = 4
        private const val INDIVIDNUMMER_START: Int = 6
        private const val KONTROLLNUMMER_START: Int = 9
        private const val MAANED_END: Int = AAR_START
        private const val AAR_END: Int = INDIVIDNUMMER_START
        private const val INDIVIDNUMMER_END: Int = KONTROLLNUMMER_START
        private const val PERSONNUMMER_START: Int = INDIVIDNUMMER_START

        private val weights1 = listOf(3, 7, 6, 1, 8, 9, 4, 5, 2, 0, 0)
        private val weights2 = listOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 0)

        private fun personnummer(pid: String) =
            parseInt(pid.substring(PERSONNUMMER_START))

        private fun dagerIMaaneden(maaned: PidMaaned, aar: Aarstall, doedfoedt: Boolean): Int {
            val maanedsnummer = maaned.value.toInt()
            val aarstall = aar.value.toInt()

            return if (maanedsnummer == Februar.MAANEDSNUMMER && doedfoedt)
                Februar.MAX_ANTALL_DAGER // år ukjent
            else
                getDaysInMonth(maanedsnummer, aarstall)
        }
    }
}