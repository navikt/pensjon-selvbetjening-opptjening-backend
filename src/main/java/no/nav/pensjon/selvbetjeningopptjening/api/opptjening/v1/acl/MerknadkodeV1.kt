package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode

enum class MerknadkodeV1(private val internalValue: MerknadCode) {

    AFP(internalValue = MerknadCode.AFP),
    REFORM(internalValue = MerknadCode.REFORM),
    UFOREGRAD(internalValue = MerknadCode.UFOREGRAD),
    DAGPENGER(internalValue = MerknadCode.DAGPENGER),
    FORSTEGANGSTJENESTE(internalValue = MerknadCode.FORSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(internalValue = MerknadCode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(internalValue = MerknadCode.GRADERT_UTTAK),
    HELT_UTTAK(internalValue = MerknadCode.HELT_UTTAK),
    UTTAK_STOPPET(internalValue = MerknadCode.UTTAK_STOPPET);

    // Følgende verdier utelates i dette API-et:
    // - INGEN_OPPTJENING
    // - OVERFORE_OMSORGSOPPTJENING
    // - TESTMERKNAD

    companion object {
        private val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: MerknadCode): MerknadkodeV1? =
            valuesByInternal[value]
    }
}