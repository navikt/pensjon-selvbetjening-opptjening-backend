package no.nav.pensjon.selvbetjeningopptjening.person.pid

import no.nav.pensjon.selvbetjeningopptjening.common.Aarstall

/**
 * Representer årsdelen i en person-ID.
 */
data class PidAar(val value: UByte) {

    init {
        require(value in 0u..99u) { "Invalid år: $value" }
    }

    infix fun plus(antall: UShort): Aarstall {
        if (antall > Aarstall.MAX_VALUE - value) throw IllegalArgumentException("Too large UShort antall: $antall")
        return Aarstall((value + antall).toUShort())
    }

    fun justert(individnummer: Int): Aarstall =
        individnummer.let {
            when {
                it < 500 -> this plus 1900u
                it < 750 && 54 < this.value.toInt() -> this plus 1800u
                it < 1000 && this.value.toInt() < 40 -> this plus 2000u
                it in 900..<1000 -> this plus 1900u
                else -> Aarstall.ZERO
            }
        }
}