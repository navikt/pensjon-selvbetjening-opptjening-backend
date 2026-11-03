package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad

@JsonInclude(NON_NULL)
data class MerknadV1(
    @field:Schema(description = "Opplistet kode for merknadstype")
    @field:NotNull
    val kode: MerknadkodeV1,

    @field:Schema(description = "Informasjon om uttaksgrad – kun angitt for kode GRADERT_UTTAK, HELT_UTTAK og UTTAK_STOPPET")
    val uttaksgrad: UttaksgradV1? = null
) {
    companion object {
        fun transferable(source: Merknad, aar: Int) =
            MerknadV1(
                kode = MerknadkodeV1.fromInternalValue(source.kode),
                uttaksgrad = source.uttaksgrad?.let { UttaksgradV1.transferable(it, aar) }
            )
    }
}