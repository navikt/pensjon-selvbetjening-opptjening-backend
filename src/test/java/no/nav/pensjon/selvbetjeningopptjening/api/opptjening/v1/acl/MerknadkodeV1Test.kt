package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknadkode

class MerknadkodeV1Test : ShouldSpec({

    should("mappe utvalgte verdier") {
        MerknadkodeV1.fromInternalValue(Merknadkode.AFP) shouldBe MerknadkodeV1.AFP
        MerknadkodeV1.fromInternalValue(Merknadkode.REFORM) shouldBe MerknadkodeV1.REFORM
        MerknadkodeV1.fromInternalValue(Merknadkode.UFOREGRAD) shouldBe MerknadkodeV1.UFOREGRAD
        MerknadkodeV1.fromInternalValue(Merknadkode.DAGPENGER) shouldBe MerknadkodeV1.DAGPENGER
        MerknadkodeV1.fromInternalValue(Merknadkode.FORSTEGANGSTJENESTE) shouldBe MerknadkodeV1.FORSTEGANGSTJENESTE
        MerknadkodeV1.fromInternalValue(Merknadkode.OMSORGSOPPTJENING) shouldBe MerknadkodeV1.OMSORGSOPPTJENING
        MerknadkodeV1.fromInternalValue(Merknadkode.GRADERT_UTTAK) shouldBe MerknadkodeV1.GRADERT_UTTAK
        MerknadkodeV1.fromInternalValue(Merknadkode.HELT_UTTAK) shouldBe MerknadkodeV1.HELT_UTTAK
        MerknadkodeV1.fromInternalValue(Merknadkode.UTTAK_STOPPET) shouldBe MerknadkodeV1.UTTAK_STOPPET
    }

    should("ikke mappe 'ingen opptjening'") {
        MerknadkodeV1.fromInternalValue(Merknadkode.INGEN_OPPTJENING) shouldBe null
    }

    should("ikke mappe 'overføre omsorgsopptjening'") {
        MerknadkodeV1.fromInternalValue(Merknadkode.OVERFORE_OMSORGSOPPTJENING) shouldBe null
    }
})