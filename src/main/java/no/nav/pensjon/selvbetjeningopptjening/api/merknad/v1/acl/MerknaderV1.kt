package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1.acl

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode

data class MerknaderV1(
    val merknaderPerAar: Map<Int, List<MerknadCodeV1>>
)

enum class MerknadCodeV1(private val internalValue: MerknadCode) {

    AFP(internalValue = MerknadCode.AFP),
    REFORM(internalValue = MerknadCode.REFORM),
    INGEN_OPPTJENING(internalValue = MerknadCode.INGEN_OPPTJENING),
    UFOREGRAD(internalValue = MerknadCode.UFOREGRAD),
    DAGPENGER(internalValue = MerknadCode.DAGPENGER),
    FORSTEGANGSTJENESTE(internalValue = MerknadCode.FORSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(internalValue = MerknadCode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(internalValue = MerknadCode.GRADERT_UTTAK),
    HELT_UTTAK(internalValue = MerknadCode.HELT_UTTAK),
    // Special value representing missing/unknown value:
    UNKNOWN(internalValue = MerknadCode.TESTMERKNAD); // TESTMERKNAD skal ikke brukes i API v1

    companion object {
        fun fromInternalValue(value: MerknadCode): MerknadCodeV1 =
            entries.singleOrNull { it.internalValue == value }
                ?: throw IllegalArgumentException("Intern verdi ikke støttet i API v1 - MerknadCode $value")
    }
}