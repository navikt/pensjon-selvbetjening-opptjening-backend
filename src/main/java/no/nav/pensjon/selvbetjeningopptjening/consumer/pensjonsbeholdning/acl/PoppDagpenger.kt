package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

data class PoppDagpenger(
    val dagpengerId: Long? = null,
    val fnr: String? = null,
    val dagpengerType: String? = null,
    val rapportType: String? = null,
    val kilde: String? = null,
    val ar: Int? = null,
    val utbetalteDagpenger: Int? = null,
    val uavkortetDagpengegrunnlag: Int? = null,
    val ferietillegg: Int? = null,
    val barnetillegg: Int? = null
)