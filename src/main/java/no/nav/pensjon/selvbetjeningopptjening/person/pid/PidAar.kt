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

    fun justert(individnummer: UShort): Aarstall =
        individnummer.let {
            when {
                it < 500u -> this plus 1900u
                it < 750u && 54u < value -> this plus 1800u
                it < 1000u && value < 40u -> this plus 2000u
                it in 900u..<1000u -> this plus 1900u
                else -> Aarstall.ZERO
            }
        }
}