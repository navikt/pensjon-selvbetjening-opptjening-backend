package no.nav.pensjon.selvbetjeningopptjening.opptjening.client

import no.nav.pensjon.selvbetjeningopptjening.health.Pingable
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt
import no.nav.pensjon.selvbetjeningopptjening.opptjening.grunnlag.OpptjeningsgrunnlagSpec

interface OpptjeningClient : Pingable {
    fun getInntektListeFromOpptjeningsgrunnlag(spec: OpptjeningsgrunnlagSpec): List<Inntekt>
}