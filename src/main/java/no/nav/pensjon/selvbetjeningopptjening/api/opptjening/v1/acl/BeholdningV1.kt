package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning

data class BeholdningV1(
    @field:Schema(description = "Beholdningsbeløp i norske kroner")
    @field:NotNull
    val beloep: Int
) {
    companion object {
        fun transferable(source: Beholdning) =
            BeholdningV1(
                beloep = source.belop.toInt()
            )
    }
}