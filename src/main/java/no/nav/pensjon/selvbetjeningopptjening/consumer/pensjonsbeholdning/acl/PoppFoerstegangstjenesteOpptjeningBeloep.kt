package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Foerstegangstjenesteopptjening

data class PoppFoerstegangstjenesteOpptjeningBeloep(
    val forstegangstjenesteOpptjeningBelopId: Long? = null,
    val ar: Int? = null,
    val belop: Double? = null,
    val forstegangstjeneste: PoppFoerstegangstjeneste? = null,
    val anvendtForstegangstjenestePeriodeListe: List<PoppFoerstegangstjenestePeriode>? = null
) {
    fun fromDto() =
        Foerstegangstjenesteopptjening(ar, belop)
}