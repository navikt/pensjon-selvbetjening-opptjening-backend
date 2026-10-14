package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Dagpengeopptjening

data class PoppDagpengerOpptjeningBeloep(
    val dagpengerOpptjeningBelopId: Long? = null,
    val ar: Int? = null,
    val belopOrdinar: Double? = null,
    val belopFiskere: Double? = null,
    val dagpengerListe: List<PoppDagpenger>? = null
) {
    fun fromDto() =
        Dagpengeopptjening(
            ar,
            belopOrdinar,
            belopFiskere
        )
}