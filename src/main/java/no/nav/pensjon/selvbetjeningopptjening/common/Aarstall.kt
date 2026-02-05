package no.nav.pensjon.selvbetjeningopptjening.common

/**
 * Representer et årstall etter vår tidsregning (0..9999).
 */
data class Aarstall(val value: UShort) {

    init {
        require(value in 0u..MAX_AAR) { "Invalid årstall: $value" }
    }

    companion object {
        private const val MAX_AAR: UInt = 9999u
        const val MAX_VALUE: UShort = 9999u
        val ZERO: Aarstall = Aarstall(0u)
    }
}
