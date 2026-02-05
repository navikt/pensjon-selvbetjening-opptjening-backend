package no.nav.pensjon.selvbetjeningopptjening.common

/**
 * Representer et enkeltsiffer i 10-tallsystemet (0..9).
 */
data class Base10Digit(val value: UByte) {

    init {
        require(value in ZERO..9u) { "Invalid decimal digit: $value" }
    }

    infix fun times(other: Base10Digit): UShort {
        if (value == ZERO || other.value == ZERO) return 0u
        if (other.value > UShort.MAX_VALUE / value) throw IllegalArgumentException("Too large multiplier: $other")
        return (value * other.value).toUShort()
    }

    private companion object {
        private const val ZERO: UByte = 0u
    }
}