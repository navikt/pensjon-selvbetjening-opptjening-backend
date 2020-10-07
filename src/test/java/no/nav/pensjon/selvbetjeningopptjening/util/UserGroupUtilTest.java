package no.nav.pensjon.selvbetjeningopptjening.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserGroupUtilTest {

    @Test
    void findUserGroup_1_when_birthYear_is_beforeNewAlder() {
        var beforeNewAlder = LocalDate.of(1942, 12, 31);
        assertEquals(USER_GROUP_1, UserGroupUtil.findUserGroup(beforeNewAlder));
    }

    @Test
    void findUserGroup_2_when_birthYear_is_beforeNewAfp() {
        var beforeNewAfp = LocalDate.of(1947, 12, 31);
        assertEquals(USER_GROUP_2, UserGroupUtil.findUserGroup(beforeNewAfp));
    }

    @Test
    void findUserGroup_3_when_birthYear_is_december1948() {
        var december1948 = LocalDate.of(1948, 12, 1);
        assertEquals(USER_GROUP_3, UserGroupUtil.findUserGroup(december1948));
    }

    @Test
    void findUserGroup_3_when_birthYear_is_beforeOvergangsregler() {
        var beforeOvergangsregler = LocalDate.of(1953, 12, 31);
        assertEquals(USER_GROUP_3, UserGroupUtil.findUserGroup(beforeOvergangsregler));
    }

    @Test
    void findUserGroup_4_when_birthYear_is_withinOvergangsregler() {
        var lastYearWithOvergangsregler = LocalDate.of(1962, 12, 31);
        assertEquals(USER_GROUP_4, UserGroupUtil.findUserGroup(lastYearWithOvergangsregler));
    }

    @Test
    void findUserGroup_5_when_birthYear_is_afterOvergangsregler() {
        var afterOvergangsregler = LocalDate.of(1963, 1, 31);
        assertEquals(USER_GROUP_5, UserGroupUtil.findUserGroup(afterOvergangsregler));
    }
}
