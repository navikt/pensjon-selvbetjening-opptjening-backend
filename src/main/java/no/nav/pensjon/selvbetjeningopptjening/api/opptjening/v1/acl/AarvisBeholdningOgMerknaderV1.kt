package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.AarvisBeholdningOgMerknader

data class AarvisBeholdningOgMerknaderV1(
    @field:Schema(description = "Beholdning og merknader for hvert opptjeningsår")
    @field:NotNull
    val perAar: Map<Int, BeholdningOgMerknaderV1>
) {
    companion object {
        fun transferable(source: AarvisBeholdningOgMerknader) =
            AarvisBeholdningOgMerknaderV1(
                perAar = source.perAar.map { (aar, data) ->
                    aar to BeholdningOgMerknaderV1.transferable(data, aar)
                }.toMap()
            )
    }
}