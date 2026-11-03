package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknader

data class BeholdningOgMerknaderV1(
    @field:Schema(description = "Beholdning som gjelder på slutten av et opptjeningsår")
    @field:NotNull
    val beholdning: BeholdningV1,

    @field:Schema(description = "Liste av merknader relatert til opptjeningen for et år")
    @field:NotNull
    val merknadListe: List<MerknadV1>
) {
    companion object {
        fun transferable(source: BeholdningOgMerknader, aar: Int) =
            BeholdningOgMerknaderV1(
                beholdning = BeholdningV1.transferable(source.beholdning),
                merknadListe = source.merknadListe.map { MerknadV1.transferable(it, aar) }
            )
    }
}