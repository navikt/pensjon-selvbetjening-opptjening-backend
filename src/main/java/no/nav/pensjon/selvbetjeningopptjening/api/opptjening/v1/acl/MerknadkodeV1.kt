package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknadkode

enum class MerknadkodeV1(private val internalValue: Merknadkode) {

    AFP(internalValue = Merknadkode.AFP),
    REFORM(internalValue = Merknadkode.REFORM),
    UFOREGRAD(internalValue = Merknadkode.UFOREGRAD),
    DAGPENGER(internalValue = Merknadkode.DAGPENGER),
    FORSTEGANGSTJENESTE(internalValue = Merknadkode.FORSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(internalValue = Merknadkode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(internalValue = Merknadkode.GRADERT_UTTAK),
    HELT_UTTAK(internalValue = Merknadkode.HELT_UTTAK),
    UTTAK_STOPPET(internalValue = Merknadkode.UTTAK_STOPPET);

    // Følgende verdier utelates i dette API-et:
    // - INGEN_OPPTJENING
    // - OVERFORE_OMSORGSOPPTJENING

    companion object {
        private val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: Merknadkode): MerknadkodeV1? =
            valuesByInternal[value]
    }
}