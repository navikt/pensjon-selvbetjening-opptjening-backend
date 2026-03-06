package no.nav.pensjon.selvbetjeningopptjening.person

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.TestFnrs
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException

class PidTest : ShouldSpec({

    should("throw PidValidationException when creating PID with invalid inputs") {
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_0, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_1, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_2, true) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_2, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_29_FEB, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_30_FEB, true) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_30_FEB, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_61_MAY, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_21_XXX, false) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_ZERO, true) }
        shouldThrow<PidValidationException> { Pid(SPECIAL_FNR_ZERO, false) }
    }

    should("not throw exception when valid D-nummer") {
        shouldNotThrow<Exception> { Pid(VALID_DNUMMER) }
    }

    should("not throw exception when valid fødselsnummer") {
        shouldNotThrow<Exception> { Pid(TestFnrs.NORMAL) }
    }

    context("constructor when special circumstances accepted") {
        should("not throw exception for D-nummer which is invalid under normal circumstances") {
            shouldNotThrow<Exception> { Pid(INVALID_DNUMMER_UNLESS_ACCEPT_SPECIAL_CIRCUMSTANCES, true) }
        }
    }

    context("toString for normal fødselsnummer") {
        should("return fødselsnummer as string") {
            Pid(TestFnrs.NORMAL, false).toString() shouldBe TestFnrs.NORMAL
        }
    }
})

private const val VALID_DNUMMER = "61080098013"
private const val INVALID_DNUMMER_UNLESS_ACCEPT_SPECIAL_CIRCUMSTANCES = "41018100000"
private const val SPECIAL_FNR_0 = "26067300000"
private const val SPECIAL_FNR_1 = "26067300001"
private const val SPECIAL_FNR_2 = "26067300002"
private const val SPECIAL_FNR_29_FEB = "29027300000"
private const val SPECIAL_FNR_30_FEB = "30027300000"
private const val SPECIAL_FNR_61_MAY = "61057300000"
private const val SPECIAL_FNR_21_XXX = "21257300000"
private const val SPECIAL_FNR_ZERO = "00000000000"
