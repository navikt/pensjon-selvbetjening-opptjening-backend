package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import java.util.*

data class PoppFoerstegangstjenestePeriode(
    val forstegangstjenestePeriodeId: Long? = null,
    val periodeType: String? = null,
    val tjenesteType: String? = null,
    val fomDato: Date? = null,
    val tomDato: Date? = null
)