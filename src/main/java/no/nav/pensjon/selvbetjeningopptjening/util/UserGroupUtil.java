package no.nav.pensjon.selvbetjeningopptjening.util;

import io.micrometer.core.instrument.Metrics;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;

import java.time.LocalDate;
import java.time.Month;

import static java.time.Month.DECEMBER;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup.*;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.*;

public class UserGroupUtil {

    public static UserGroup findUserGroup(LocalDate birthDate) {
        return findUserGroup(birthDate.getYear(), birthDate.getMonth());
    }

    private static UserGroup findUserGroup(int year, Month month) {
        if (year < FIRST_BIRTHYEAR_WITH_NEW_ALDER) {
            incrementCounter("Født før 1954");
            return USER_GROUP_1;
        }

        if (year <= FIRST_BIRTHYEAR_WITH_NEW_AFP) {
            incrementCounter("Født før 1954");

            return year == FIRST_BIRTHYEAR_WITH_NEW_AFP && month.equals(DECEMBER)
                    ? USER_GROUP_3
                    : USER_GROUP_2;
        }

        if (year < FIRST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            incrementCounter("Født før 1954");
            return USER_GROUP_3;
        }

        if (year <= LAST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            incrementCounter("Født 1954-1962");
            return USER_GROUP_4;
        }

        incrementCounter("Født etter 1962");
        return USER_GROUP_5;
    }

    private static void incrementCounter(String group) {
        Metrics.counter("pensjon_selvbetjening_user_group_counter", "user-group", group).increment();
    }
}
