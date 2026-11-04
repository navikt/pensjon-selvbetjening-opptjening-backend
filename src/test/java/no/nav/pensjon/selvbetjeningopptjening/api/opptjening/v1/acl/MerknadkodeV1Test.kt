package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode

class MerknadkodeV1Test : ShouldSpec({

    should("mappe utvalgte verdier") {
        MerknadkodeV1.fromInternalValue(MerknadCode.AFP) shouldBe MerknadkodeV1.AFP
        MerknadkodeV1.fromInternalValue(MerknadCode.REFORM) shouldBe MerknadkodeV1.REFORM
        MerknadkodeV1.fromInternalValue(MerknadCode.UFOREGRAD) shouldBe MerknadkodeV1.UFOREGRAD
        MerknadkodeV1.fromInternalValue(MerknadCode.DAGPENGER) shouldBe MerknadkodeV1.DAGPENGER
        MerknadkodeV1.fromInternalValue(MerknadCode.FORSTEGANGSTJENESTE) shouldBe MerknadkodeV1.FORSTEGANGSTJENESTE
        MerknadkodeV1.fromInternalValue(MerknadCode.OMSORGSOPPTJENING) shouldBe MerknadkodeV1.OMSORGSOPPTJENING
        MerknadkodeV1.fromInternalValue(MerknadCode.GRADERT_UTTAK) shouldBe MerknadkodeV1.GRADERT_UTTAK
        MerknadkodeV1.fromInternalValue(MerknadCode.HELT_UTTAK) shouldBe MerknadkodeV1.HELT_UTTAK
        MerknadkodeV1.fromInternalValue(MerknadCode.UTTAK_STOPPET) shouldBe MerknadkodeV1.UTTAK_STOPPET
    }

    should("ikke mappe 'ingen opptjening'") {
        MerknadkodeV1.fromInternalValue(MerknadCode.INGEN_OPPTJENING) shouldBe null
    }

    should("ikke mappe 'overføre omsorgsopptjening'") {
        MerknadkodeV1.fromInternalValue(MerknadCode.OVERFORE_OMSORGSOPPTJENING) shouldBe null
    }

    should("ikke mappe 'testmerknad'") {
        MerknadkodeV1.fromInternalValue(MerknadCode.TESTMERKNAD) shouldBe null
    }
})