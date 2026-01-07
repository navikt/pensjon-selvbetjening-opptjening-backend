package no.nav.pensjon.selvbetjeningopptjening.person.group

import io.micrometer.core.instrument.Metrics
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup
import java.time.LocalDate
import java.time.Month

object UserGroupUtil {

    /**
     * Birthyear of the first group having a right to new "privat AFP".
     * Reglene ble endret rundt 2011, så for personer født i 1948 er det spesifikke kvalifikasjonskrav.
     */
    private const val FIRST_BIRTHYEAR_WITH_NEW_PRIVAT_AFP: Int = 1948

    /**
     * Birthyear of the first group having a right to new Alder
     */
    private const val FIRST_BIRTHYEAR_WITH_NEW_ALDER: Int = 1943

    /**
     * The first birthyear that has "overgangsregler for opptjening"
     */
    private const val FIRST_BIRTHYEAR_WITH_OVERGANGSREGLER: Int = 1954

    /**
     * The last birthyear that has "overgangsregler for opptjening"
     */
    private const val LAST_BIRTHYEAR_WITH_OVERGANGSREGLER: Int = 1962

    fun findUserGroup(birthDate: LocalDate): UserGroup =
        findUserGroup(birthDate.year, birthDate.month)

    private fun findUserGroup(year: Int, month: Month): UserGroup {
        if (year < FIRST_BIRTHYEAR_WITH_NEW_ALDER) {
            incrementCounter("Født før 1954")
            return UserGroup.USER_GROUP_1
        }

        if (year <= FIRST_BIRTHYEAR_WITH_NEW_PRIVAT_AFP) {
            incrementCounter("Født før 1954")

            return if (year == FIRST_BIRTHYEAR_WITH_NEW_PRIVAT_AFP && month == Month.DECEMBER) UserGroup.USER_GROUP_3 else UserGroup.USER_GROUP_2
        }

        if (year < FIRST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            incrementCounter("Født før 1954")
            return UserGroup.USER_GROUP_3
        }

        if (year <= LAST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            incrementCounter("Født 1954-1962")
            return UserGroup.USER_GROUP_4
        }

        incrementCounter("Født etter 1962")
        return UserGroup.USER_GROUP_5
    }

    private fun incrementCounter(group: String) {
        Metrics.counter("pensjon_selvbetjening_user_group_counter", "user-group", group).increment()
    }
}
