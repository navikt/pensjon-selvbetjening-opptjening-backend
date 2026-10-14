package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt

data class PoppInntekt(
    val inntektId: Long? = null,
    val fnr: String? = null,
    val kilde: String? = null,
    val kommune: String? = null,
    val piMerke: String? = null,
    val inntektAr: Int? = null,
    val belop: Long? = null,
    val inntektType: String? = null
) {
    fun fromDto() =
        Inntekt(
            inntektAr,
            inntektType,
            belop
        )
}