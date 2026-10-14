package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntektsopptjening

data class PoppInntektOpptjeningBeloep(
    var inntektOpptjeningBelopId: Long? = null,
    val ar: Int? = null,
    val belop: Double? = null,
    val sumPensjonsgivendeInntekt: PoppInntekt? = null,
    val inntektListe: List<PoppInntekt>? = null
){
    fun fromDto() =
        Inntektsopptjening(
            ar,
            belop,
            sumPensjonsgivendeInntekt?.fromDto()
        )
}