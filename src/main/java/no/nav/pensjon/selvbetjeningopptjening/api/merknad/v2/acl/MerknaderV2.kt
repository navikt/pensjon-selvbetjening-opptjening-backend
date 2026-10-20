package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v2.acl

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknader
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad

data class MerknaderV2(
    val merknaderPerAar: Map<Int, List<MerknadV2>>
) {
    companion object {
        fun transferable(source: Merknader) =
            MerknaderV2(
                merknaderPerAar = source.perAar.map { (aar, code) ->
                    aar to code.map(MerknadV2::transferable)
                }.toMap()
            )
    }
}

data class MerknadV2(
    val kode: MerknadCodeV2,
    val uttaksgrad: UttaksgradV2?
) {
    companion object {
        fun transferable(source: Merknad) =
            MerknadV2(
                kode = MerknadCodeV2.fromInternalValue(source.kode),
                uttaksgrad = source.uttaksgrad?.let(UttaksgradV2::transferable)
            )
    }
}

data class UttaksgradV2(
    val grad: Int,
    val kalendermaanedFom: Int
) {
    companion object {
        fun transferable(source: Uttaksgrad) =
            UttaksgradV2(
                grad = source.uttaksgrad,
                kalendermaanedFom = source.fom.monthValue
            )
    }
}

enum class MerknadCodeV2(private val internalValue: MerknadCode) {

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
        fun fromInternalValue(value: MerknadCode): MerknadCodeV2 =
            entries.singleOrNull { it.internalValue == value }
                ?: throw IllegalArgumentException("Intern verdi ikke støttet i API v2 - MerknadCode $value")
    }
}