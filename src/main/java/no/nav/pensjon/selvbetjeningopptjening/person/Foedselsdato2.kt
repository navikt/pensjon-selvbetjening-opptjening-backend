package no.nav.pensjon.selvbetjeningopptjening.person

import java.time.LocalDate

data class Foedselsdato2(
    val value: LocalDate,
    val basedOnYearOnly: Boolean = false
){
    constructor(value: LocalDate) : this(value, basedOnYearOnly = false)
    constructor(aar: Int) : this(value = LocalDate.of(aar, 1, 1), basedOnYearOnly = true)
}
