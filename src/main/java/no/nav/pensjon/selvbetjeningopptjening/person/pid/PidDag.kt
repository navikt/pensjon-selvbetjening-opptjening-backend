package no.nav.pensjon.selvbetjeningopptjening.person.pid

import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.isDayOfMonth

/**
 * Representer dagdelen i en person-ID.
 */
data class PidDag(val value: UByte) {

    init {
        require(value in 0u..99u) { "Invalid PID-dag: $value" }
    }

    fun justert(): PidDag =
        when {
            isDnrDag() -> PidDag((value - DNR_DAG_TILLEGG).toUByte())
            else -> this
        }

    /**
     * A D-nummer (DNR) is used as the PID for foreigners living in Norway.
     * In a DNR the number 4 has been added to the first digit in the PID;
     * otherwise it is similar to an FNR for native Norwegians.
     * Note that this method may not work on weakly validated PIDs (using a 'special circumstances' flag),
     * as such PIDs can never be guaranteed.
     */
    fun isDnrDag(): Boolean =
        isDayOfMonth((value - DNR_DAG_TILLEGG).toInt())

    fun within(min: Int, max: Int): Boolean =
        value.toInt() in min..max

    companion object {
        private const val DAG_START: Int = 0
        private const val DAG_END: Int = 2
        private const val DNR_DAG_TILLEGG: UByte = 40u

        fun from(pid: String) =
            PidDag(pid.substring(DAG_START, DAG_END).toUByte())
    }
}