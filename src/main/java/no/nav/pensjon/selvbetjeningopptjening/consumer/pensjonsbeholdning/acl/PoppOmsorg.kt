package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Omsorg

data class PoppOmsorg(
    val omsorgId: Long? = null,
    val fnr: String? = null,
    val fnrOmsorgFor: String? = null,
    val omsorgType: String? = null,
    val kilde: String? = null,
    val ar: Int? = null,
) {
    fun fromDto() =
        Omsorg(omsorgType)
}