package no.nav.pensjon.selvbetjeningopptjening.person.group

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup
import java.time.LocalDate

class UserGroupUtilTest : ShouldSpec({

    should("return user group 1 when birthYear is before new alder") {
        val beforeNewAlder = LocalDate.of(1942, 12, 31)
        UserGroupUtil.findUserGroup(beforeNewAlder) shouldBe UserGroup.USER_GROUP_1
    }

    should("return user group 2 when birthYear is before new privat AFP") {
        val beforeNewAfp = LocalDate.of(1947, 12, 31)
        UserGroupUtil.findUserGroup(beforeNewAfp) shouldBe UserGroup.USER_GROUP_2
    }

    should("return user group 3 when birthYear is December 1948") {
        val december1948 = LocalDate.of(1948, 12, 1)
        UserGroupUtil.findUserGroup(december1948) shouldBe UserGroup.USER_GROUP_3
    }

    should("return user group 3 when birthYear is before overgangsregler") {
        val beforeOvergangsregler = LocalDate.of(1953, 12, 31)
        UserGroupUtil.findUserGroup(beforeOvergangsregler) shouldBe UserGroup.USER_GROUP_3
    }

    should("return user group 4 when birthYear is within overgangsregler") {
        val lastYearWithOvergangsregler = LocalDate.of(1962, 12, 31)
        UserGroupUtil.findUserGroup(lastYearWithOvergangsregler) shouldBe UserGroup.USER_GROUP_4
    }

    should("return user group 5 when birthYear is after overgangsregler") {
        val afterOvergangsregler = LocalDate.of(1963, 1, 31)
        UserGroupUtil.findUserGroup(afterOvergangsregler) shouldBe UserGroup.USER_GROUP_5
    }
})
