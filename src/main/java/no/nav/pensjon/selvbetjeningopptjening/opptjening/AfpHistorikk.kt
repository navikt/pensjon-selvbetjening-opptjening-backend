package no.nav.pensjon.selvbetjeningopptjening.opptjening

import java.time.LocalDate
import java.util.function.Supplier

data class AfpHistorikk(
    val virkningFomDate: LocalDate,
    val virkningTomDate: LocalDate?
) {
    val startYear: Int = virkningFomDate.year

    fun getEndYearOrDefault(defaultYear: Supplier<Int>): Int =
        virkningTomDate?.year ?: defaultYear.get()
}