package no.nav.pensjon.selvbetjeningopptjening.util;

import java.time.LocalDate;
import java.time.Month;

import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;

public class UserGroupUtil {
    public static UserGroup findUserGroup(LocalDate fodselsdato) {
        int fodselsar = fodselsdato.getYear();
        int fodselsmonth = fodselsdato.getMonth().getValue();

        if (fodselsar < Constants.FIRST_BIRTHYEAR_WITH_NEW_ALDER) {
            return UserGroup.USER_GROUP_1;
        } else if (fodselsar <= Constants.FIRST_BIRTHYEAR_WITH_NEW_AFP) {
            /**
             * user belongs to userGroup 3 if user is borned in december 1948
             */
            if (fodselsar == Constants.FIRST_BIRTHYEAR_WITH_NEW_AFP && fodselsmonth > Month.NOVEMBER.getValue()) {
                return UserGroup.USER_GROUP_3;
            }
            return UserGroup.USER_GROUP_2;
        } else if (fodselsar < Constants.FIRST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            return UserGroup.USER_GROUP_3;
        } else if (fodselsar <= Constants.LAST_BIRTHYEAR_WITH_OVERGANGSREGLER) {
            return UserGroup.USER_GROUP_4;
        } else {
            return UserGroup.USER_GROUP_5;
        }
    }
}
