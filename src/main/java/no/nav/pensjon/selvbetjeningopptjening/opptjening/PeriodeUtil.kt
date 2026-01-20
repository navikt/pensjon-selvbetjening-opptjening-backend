package no.nav.pensjon.selvbetjeningopptjening.opptjening

import java.time.LocalDate

object PeriodeUtil {

    fun <T : Periode> sortPerioderByFomDate(perioder: List<T>): List<T> {
        val copy: MutableList<T> = perioder.toMutableList()

        if (copy.size <= 1)
            return copy

        copy.sortWith(Comparator { a: T, b: T ->
            a.getFomDato().compareTo(b.getFomDato())
        })

        return copy
    }

    fun isPeriodeWithinInterval(periode: Periode, start: LocalDate?, end: LocalDate?): Boolean {
        val fom = periode.getFomDato()
        val tom = periode.getTomDato()

        return fom.isAfter(start)
                && (tom == null && (end == null || end.isAfter(fom))
                || (tom != null && (end == null || tom.isBefore(end) || tom.isEqual(end))))
    }
}