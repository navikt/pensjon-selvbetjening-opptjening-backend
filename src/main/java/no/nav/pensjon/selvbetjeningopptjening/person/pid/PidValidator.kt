package no.nav.pensjon.selvbetjeningopptjening.person.pid

import org.springframework.util.StringUtils.trimAllWhitespace

object PidValidator {

    /**
     * "Special circumstances" means that the personnummer part (last 5 digits of FNR) does not follow the normal rules,
     * but has a special value like 00000 or 00001.
     */
    fun isValidPid(value: String?, acceptSpecialCircumstances: Boolean = false): Boolean =
        value
            ?.let(::trimAllWhitespace)
            ?.let(::StructuredPid)
            ?.isValid(acceptSpecialCircumstances) == true

    fun datoPart(pid: String): String =
        StructuredPid(trimAllWhitespace(pid)).justertDato()
}