package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.grunnlag.OpptjeningsgrunnlagSpec

data class PoppOpptjeningsgrunnlagSpec(
    val fnr: String,
    val grunnlagstypeListe: List<PoppOpptjeningsgrunnlagType>?,
    val fomAr: Int?,
    val tomAr: Int?
) {
    companion object {
        fun fromInternalValue(source: OpptjeningsgrunnlagSpec) =
            PoppOpptjeningsgrunnlagSpec(
                fnr = source.pid.pid,
                grunnlagstypeListe = source.grunnlagstypeListe.map(PoppOpptjeningsgrunnlagType::fromInternalValue)
                    .takeUnless { it.isEmpty() }, // "wildcard" is represented by null, not empty list
                fomAr = source.fomAar,
                tomAr = source.tomAar
            )
    }
}