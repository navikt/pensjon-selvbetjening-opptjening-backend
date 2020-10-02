package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;
import java.time.Month;

import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;

public class UserGroupUtil {

    public static UserGroup findUserGroup(LocalDate birthDate) {
        int year = birthDate.getYear();
        int month = birthDate.getMonth().getValue();

        if (year < Constants.FIRST_BIRTHYEAR_WITH_NEW_ALDER) {
            return UserGroup.USER_GROUP_1;
        }

        if (year <= Constants.FIRST_BIRTHYEAR_WITH_NEW_AFP) {
            // User belongs to user group 3 if user is born in December 1948
            if (year == Constants.FIRST_BIRTHYEAR_WITH_NEW_AFP && month > Month.NOVEMBER.getValue()) {
                return UserGroup.USER_GROUP_3;
            }
            return UserGroup.USER_GROUP_2;
        }

        if (year < Constants.FIRST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            return UserGroup.USER_GROUP_3;
        }

        if (year <= Constants.LAST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            return UserGroup.USER_GROUP_4;
        }

        return UserGroup.USER_GROUP_5;
    }
}
