package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v2.acl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknader
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad

data class MerknaderV2(
    @field:Schema(description = "Merknader som er knyttet til opptjeningen, gruppert per kalenderår")
    val merknaderPerAar: Map<Int, List<MerknadV2>>
) {
    companion object {
        fun transferable(source: Merknader) =
            MerknaderV2(
                merknaderPerAar = source.perAar.map { (aar, code) ->
                    aar to code.map { MerknadV2.transferable(it, aar) }
                }.toMap()
            )
    }
}

@JsonInclude(NON_NULL)
data class MerknadV2(
    @field:Schema(description = "Enumerert kode for merknadstype")
    @field:NotNull
    val kode: MerknadCodeV2,

    @field:Schema(description = "Informasjon om uttaksgrad – kun angitt for kode GRADERT_UTTAK, HELT_UTTAK og UTTAK_STOPPET")
    val uttaksgrad: UttaksgradV2?
) {
    companion object {
        fun transferable(source: Merknad, aar: Int) =
            MerknadV2(
                kode = MerknadCodeV2.fromInternalValue(source.kode),
                uttaksgrad = source.uttaksgrad?.let { UttaksgradV2.transferable(it, aar) }
            )
    }
}

@JsonInclude(NON_NULL)
data class UttaksgradV2(
    @field:Schema(description = "Uttaksgrad angitt i prosent")
    @field:NotNull
    val grad: Int,

    @field:Schema(description = "Hvilken kalendermåned (1..12) uttaket med denne graden startet (fra og med) - kun angitt for året da uttaket startet")
    val kalendermaanedFom: Int?
) {
    companion object {
        fun transferable(source: Uttaksgrad, aar: Int) =
            UttaksgradV2(
                grad = source.grad,
                kalendermaanedFom = source.fom.let { if (it.year == aar) it.monthValue else null }
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