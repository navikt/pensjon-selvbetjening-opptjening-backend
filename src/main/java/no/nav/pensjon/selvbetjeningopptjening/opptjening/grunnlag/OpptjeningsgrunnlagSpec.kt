package no.nav.pensjon.selvbetjeningopptjening.opptjening.grunnlag

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

data class OpptjeningsgrunnlagSpec(
    val pid: Pid,
    val grunnlagstypeListe: List<OpptjeningsgrunnlagType> = emptyList(),
    val fomAar: Int? = null,
    val tomAar: Int? = null
)