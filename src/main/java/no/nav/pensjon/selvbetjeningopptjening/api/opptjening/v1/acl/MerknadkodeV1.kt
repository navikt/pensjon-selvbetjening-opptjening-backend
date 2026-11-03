package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode

enum class MerknadkodeV1(private val internalValue: MerknadCode) {

    AFP(internalValue = MerknadCode.AFP),
    REFORM(internalValue = MerknadCode.REFORM),
    INGEN_OPPTJENING(internalValue = MerknadCode.INGEN_OPPTJENING),
    UFOREGRAD(internalValue = MerknadCode.UFOREGRAD),
    DAGPENGER(internalValue = MerknadCode.DAGPENGER),
    FORSTEGANGSTJENESTE(internalValue = MerknadCode.FORSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(internalValue = MerknadCode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(internalValue = MerknadCode.GRADERT_UTTAK),
    HELT_UTTAK(internalValue = MerknadCode.HELT_UTTAK),
    UTTAK_STOPPET(internalValue = MerknadCode.UTTAK_STOPPET),

    // Special value representing missing/unknown value:
    UNKNOWN(internalValue = MerknadCode.TESTMERKNAD); // TESTMERKNAD skal ikke brukes i API v2

    companion object {
        fun fromInternalValue(value: MerknadCode): MerknadkodeV1 =
            entries.singleOrNull { it.internalValue == value }
                ?: throw IllegalArgumentException("Intern verdi ikke støttet i API v2 - MerknadCode $value")
    }
}