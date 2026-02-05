package no.nav.pensjon.selvbetjeningopptjening.person.pid

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.TestFnrs

class PidValidatorTest : ShouldSpec({

    context("isValidPid when PID has special circumstances") {
        should("return 'false' when special circumstances not allowed") {
            PidValidator.isValidPid(value = SPECIAL_FNR_0, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_1, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_2, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_29_FEB, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_30_FEB, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_61_MAY, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_21_XXX, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_ZERO, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_3_JAN_73, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_3_JAN_76, acceptSpecialCircumstances = false) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_8_JUN_57, acceptSpecialCircumstances = false) shouldBe false
        }

        should("use default 'false' for 'acceptSpecialCircumstances'") {
            PidValidator.isValidPid(value = SPECIAL_FNR_0) shouldBe false
        }

        should("return 'true' when special circumstances allowed") {
            PidValidator.isValidPid(value = SPECIAL_FNR_0, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = SPECIAL_FNR_1, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = SPECIAL_FNR_29_FEB, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = SPECIAL_FNR_61_MAY, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = SPECIAL_FNR_21_XXX, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = SPECIAL_FNR_3_JAN_73, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = SPECIAL_FNR_3_JAN_76, acceptSpecialCircumstances = true) shouldBe true
        }
    }

    context("isValidPid when PID has illegal special circumstances") {
        should("return 'false'") {
            PidValidator.isValidPid(value = SPECIAL_FNR_ZERO, acceptSpecialCircumstances = true) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_30_FEB, acceptSpecialCircumstances = true) shouldBe false
            PidValidator.isValidPid(value = SPECIAL_FNR_2, acceptSpecialCircumstances = true) shouldBe false
        }
    }

    context("isValidPid when valid input") {
        should("return 'true' when valid fødselsnummer") {
            PidValidator.isValidPid(value = TestFnrs.NORMAL) shouldBe true
        }

        should("return 'true' when valid Test-Norge-fødselsnummer") {
            PidValidator.isValidPid(value = TESTNORGE_FNR) shouldBe true
        }

        should("return 'true' when valid Dolly-fødselsnummer") {
            PidValidator.isValidPid(value = DOLLY_FNR) shouldBe true
        }

        should("return 'true' when valid BOST-nummer") {
            PidValidator.isValidPid(value = MONTH_ABOVE_9_BOST_NR) shouldBe true
            PidValidator.isValidPid(value = MONTH_BELOW_10_BOST_NR) shouldBe true
        }

        should("return 'true' when valid D-nummer") {
            PidValidator.isValidPid(value = D_NUMMER_VALID_0) shouldBe true
        }
    }

    context("isValidPid when D-nummer ending with 00000N") {
        should("return 'true' when not accepting special circumstances") {
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_1, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_2, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_3, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_4, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_5, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_6, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_7, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_8, acceptSpecialCircumstances = false) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_9, acceptSpecialCircumstances = false) shouldBe true
        }

        should("return 'true' when accepting special circumstances") {
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_1, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_2, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_3, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_4, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_5, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_6, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_7, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_8, acceptSpecialCircumstances = true) shouldBe true
            PidValidator.isValidPid(value = D_NUMMER_VALID_LOW_PNR_9, acceptSpecialCircumstances = true) shouldBe true
        }
    }

    context("getDatoPart") {
        should("return adjusted dato part when valid 'Test-Norge'-fødselsnummer as input") {
            PidValidator.datoPart(pid = TESTNORGE_FNR) shouldBe "29081955"
        }

        should("return adjusted dato part when valid Dolly-fødselsnummer as input") {
            PidValidator.datoPart(pid = DOLLY_FNR) shouldBe "28051978"
        }

        should("return adjusted dato part when valid BOST-fødselsnummer as input") {
            PidValidator.datoPart(pid = MONTH_BELOW_10_BOST_NR) shouldBe "04051901"
            PidValidator.datoPart(pid = MONTH_ABOVE_9_BOST_NR) shouldBe "01121972"
        }
    }
})

private const val MONTH_BELOW_10_BOST_NR = "04250100286"
private const val MONTH_ABOVE_9_BOST_NR = "01327200336"
private const val SPECIAL_FNR_8_JUN_57 = "08065700000"
private const val SPECIAL_FNR_0 = "26067300000"
private const val SPECIAL_FNR_1 = "26067300001"
private const val SPECIAL_FNR_2 = "26067300002"
private const val SPECIAL_FNR_29_FEB = "29027300000"
private const val SPECIAL_FNR_30_FEB = "30027300000"
private const val SPECIAL_FNR_61_MAY = "61057300000"
private const val SPECIAL_FNR_21_XXX = "21257300000"
private const val SPECIAL_FNR_ZERO = "00000000000"
private const val SPECIAL_FNR_3_JAN_76 = "03017600000"
private const val SPECIAL_FNR_3_JAN_73 = "03017300000" // Passes the mod11 check, but not the special circumstance check
private const val D_NUMMER_VALID_0 = "41060094231"
private const val D_NUMMER_VALID_LOW_PNR_1 = "41015800001"
private const val D_NUMMER_VALID_LOW_PNR_2 = "57022800002"
private const val D_NUMMER_VALID_LOW_PNR_3 = "64025000003"
private const val D_NUMMER_VALID_LOW_PNR_4 = "58044400004"
private const val D_NUMMER_VALID_LOW_PNR_5 = "61073600005"
private const val D_NUMMER_VALID_LOW_PNR_6 = "67045600006"
private const val D_NUMMER_VALID_LOW_PNR_7 = "46073400007"
private const val D_NUMMER_VALID_LOW_PNR_8 = "70103900008"
private const val D_NUMMER_VALID_LOW_PNR_9 = "41033100009"
private const val TESTNORGE_FNR = "29885596930" // +80 in "måned" value
private const val DOLLY_FNR = "28457848279" // +40 in "måned" value
