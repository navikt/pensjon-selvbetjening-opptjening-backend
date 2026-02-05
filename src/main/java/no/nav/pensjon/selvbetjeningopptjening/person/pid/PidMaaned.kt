package no.nav.pensjon.selvbetjeningopptjening.person.pid

/**
 * Representer månedsdelen i en person-ID.
 */
data class PidMaaned(val value: UByte) {

    init {
        require(value in 0u..99u) { "Invalid PID-måned: $value" }
    }

    infix fun plus(justering: UByte): UByte {
        if (justering > UByte.MAX_VALUE - value) throw IllegalArgumentException("Too large UByte justering: $justering")
        return (value + justering).toUByte()
    }

    infix fun minus(justering: UByte): UByte {
        if (justering > value) throw IllegalArgumentException("Too large UByte justering: $justering")
        return (value - justering).toUByte()
    }

    fun justert(): PidMaaned =
        when {
            isValid(adjustment = DOLLY_FNR_MAANED_ADDITION) ->
                this minus DOLLY_FNR_MAANED_ADDITION

            isValid(adjustment = NPID_SYNTHETIC_FNR_MAANED_ADDITION) ->
                this minus NPID_SYNTHETIC_FNR_MAANED_ADDITION

            isValid(adjustment = TESTNORGE_FNR_MAANED_ADDITION) ->
                this minus TESTNORGE_FNR_MAANED_ADDITION

            isValid(adjustment = BOST_NUMMER_MAANED_ADDITION) ->
                this minus BOST_NUMMER_MAANED_ADDITION

            else -> value
        }.let(::PidMaaned)

    private fun isValid(adjustment: UByte): Boolean =
        value - adjustment in 1u..MONTHS_PER_YEAR

    private companion object {
        private const val BOST_NUMMER_MAANED_ADDITION: UByte = 20u // BOST is a legacy PID type (replaced by NPID)
        private const val DOLLY_FNR_MAANED_ADDITION: UByte = 40u
        private const val NPID_SYNTHETIC_FNR_MAANED_ADDITION: UByte = 60u
        private const val TESTNORGE_FNR_MAANED_ADDITION: UByte = 80u
        private const val MONTHS_PER_YEAR: UInt = 12u
    }
}