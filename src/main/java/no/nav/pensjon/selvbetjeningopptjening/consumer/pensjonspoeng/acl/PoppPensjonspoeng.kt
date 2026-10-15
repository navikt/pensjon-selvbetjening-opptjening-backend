package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.acl

import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl.PoppInntekt
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl.PoppOmsorg
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng

data class PoppPensjonspoeng(
    val pensjonspoengId: Long? = null,
    val fnr: String? = null,
    val fnrOmsorgFor: String? = null,
    val kilde: String? = null,
    val pensjonspoengType: String? = null,
    val inntekt: PoppInntekt? = null,
    val omsorg: PoppOmsorg? = null,
    val ar: Int? = null,
    val anvendtPi: Int? = null,
    val poeng: Double? = null,
    val maxUforegrad: Int? = null
) {
    fun fromDto() =
        Pensjonspoeng(
            ar,
            pensjonspoengType,
            poeng,
            inntekt?.fromDto(),
            omsorg?.fromDto()
        )
}