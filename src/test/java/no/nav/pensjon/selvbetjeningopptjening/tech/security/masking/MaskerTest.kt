package no.nav.pensjon.selvbetjeningopptjening.tech.security.masking

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

class MaskerTest : FunSpec({

    test("maskFnr masks other fnr digits than birthDate") {
        Masker.maskFnr("12345678901") shouldBe "123456*****"
        Masker.maskFnr(Pid("04925398980")) shouldBe "049253*****"
    }

    test("maskFnr masks all digits in nonFnr") {
        Masker.maskFnr("1234567890") shouldBe "****** (length 10)"
    }

    test("maskFnr removes control characters") {
        Masker.maskFnr("\n0492539\r8980\t") shouldBe "049253*****"
    }

    test("maskFnr handles empty string") {
        Masker.maskFnr("") shouldBe "****** (length 0)"
    }

    test("maskFnr returns textNull for valueNull") {
        Masker.maskFnr(null as Pid?) shouldBe "null"
        Masker.maskFnr(null as String?) shouldBe "null"
    }
})
