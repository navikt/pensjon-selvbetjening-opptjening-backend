package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import java.util.*

data class PoppFoerstegangstjeneste(
    val forstegangstjenesteId: Long? = null,
    val fnr: String? = null,
    val forstegangstjenestePeriodeListe: List<PoppFoerstegangstjenestePeriode>? = null,
    val kilde: String? = null,
    val rapportType: String? = null,
    val tjenestestartDato: Date? = null,
    val dimitteringDato: Date? = null
)