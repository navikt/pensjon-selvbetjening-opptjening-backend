package no.nav.pensjon.selvbetjeningopptjening.person.pid

import no.nav.pensjon.selvbetjeningopptjening.common.Base10Digit

/**
 * Representer et enkeltsiffer i en person-ID,
 * med tilhørende vekttall for validering.
 */
data class PidDigit(
    val base10Digit: Base10Digit,
    val weight1: Base10Digit,
    val weight2: Base10Digit
) {
    fun weighted1(): UShort = base10Digit.times(weight1)
    fun weighted2(): UShort = base10Digit.times(weight2)
}