package no.nav.pensjon.selvbetjeningopptjening.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.TestFnrs

class PidValidatorTest : ShouldSpec({

    context("isValidPid when PID has special circumstances") {
        should("return 'false' when special circumstances not allowed") {
            PidValidator.isValidPid(SPECIAL_FNR_0, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_0) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_1, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_1) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_2) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_29_FEB) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_29_FEB, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_30_FEB) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_30_FEB, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_61_MAY) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_61_MAY, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_21_XXX) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_21_XXX, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_ZERO) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_ZERO, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_3_JAN_73, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_3_JAN_76, false) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_8_JUN_57, false) shouldBe false
        }

        should("return 'true' when special circumstances allowed") {
            PidValidator.isValidPid(SPECIAL_FNR_0, true) shouldBe true
            PidValidator.isValidPid(SPECIAL_FNR_1, true) shouldBe true
            PidValidator.isValidPid(SPECIAL_FNR_29_FEB, true) shouldBe true
            PidValidator.isValidPid(SPECIAL_FNR_61_MAY, true) shouldBe true
            PidValidator.isValidPid(SPECIAL_FNR_21_XXX, true) shouldBe true
            PidValidator.isValidPid(SPECIAL_FNR_3_JAN_73, true) shouldBe true
            PidValidator.isValidPid(SPECIAL_FNR_3_JAN_76, true) shouldBe true
        }
    }

    context("isValidPid when PID has illegal special circumstances") {
        should("return 'false'") {
            PidValidator.isValidPid(SPECIAL_FNR_ZERO, true) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_30_FEB, true) shouldBe false
            PidValidator.isValidPid(SPECIAL_FNR_2, true) shouldBe false
        }
    }

    context("isValidPid when valid input") {
        should("return 'true' when valid fødselsnummer") {
            PidValidator.isValidPid(TestFnrs.NORMAL) shouldBe true
        }

        should("return 'true' when valid Test-Norge-fødselsnummer") {
            PidValidator.isValidPid(TESTNORGE_FNR) shouldBe true
        }

        should("return 'true' when valid Dolly-fødselsnummer") {
            PidValidator.isValidPid(DOLLY_FNR) shouldBe true
        }

        should("return 'true' when valid BOST-nummer") {
            PidValidator.isValidPid(MONTH_ABOVE_9_BOST_NR) shouldBe true
            PidValidator.isValidPid(MONTH_BELOW_10_BOST_NR) shouldBe true
        }

        should("return 'true' when valid D-nummer") {
            PidValidator.isValidPid(D_NUMMER_VALID_0) shouldBe true
        }
    }

    context("isValidPid when D-nummer ending with 00000N") {
        should("return 'true' when not accepting special circumstances") {
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_1, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_2, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_3, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_4, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_5, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_6, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_7, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_8, false) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_9, false) shouldBe true
        }

        should("return 'true' when accepting special circumstances") {
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_1, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_2, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_3, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_4, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_5, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_6, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_7, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_8, true) shouldBe true
            PidValidator.isValidPid(D_NUMMER_VALID_LOW_PNR_9, true) shouldBe true
        }
    }

    context("getDatoPart") {
        should("return adjustedDatoPart when valid testNorgeFnr as input") {
            PidValidator.getDatoPart(TESTNORGE_FNR) shouldBe "29081955"
        }

        should("return adjustedDatoPart when valid dollyFnr as input") {
            PidValidator.getDatoPart(DOLLY_FNR) shouldBe "28051978"
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
