package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Omsorgsopptjening

data class PoppOmsorgOpptjeningBeloep(
    val omsorgOpptjeningBelopId: Long? = null,
    val ar: Int? = null,
    val belop: Double? = null,
    val omsorgOpptjeningInnskudd: Double? = null,
    val omsorgListe: List<PoppOmsorg>? = null
) {
    fun fromDto() =
        Omsorgsopptjening(
            ar,
            belop,
            omsorgListe.orEmpty().map { it.fromDto() }
        )
}