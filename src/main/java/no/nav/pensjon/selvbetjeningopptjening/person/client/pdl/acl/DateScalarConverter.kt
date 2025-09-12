package no.nav.pensjon.selvbetjeningopptjening.person.client.pdl.acl

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateScalarConverter {
    fun toJson(value: LocalDate): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE)
    fun toScalar(rawValue: String): LocalDate = LocalDate.parse(rawValue, DateTimeFormatter.ISO_LOCAL_DATE)
}
