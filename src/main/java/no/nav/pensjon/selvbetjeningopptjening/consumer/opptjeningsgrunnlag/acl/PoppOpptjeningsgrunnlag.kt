package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.acl

import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl.PoppInntekt

data class PoppOpptjeningsgrunnlag(
    val inntektListe: List<PoppInntekt>? = null
)