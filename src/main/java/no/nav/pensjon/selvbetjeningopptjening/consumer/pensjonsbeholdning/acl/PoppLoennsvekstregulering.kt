package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Lonnsvekstregulering
import java.time.LocalDate

data class PoppLoennsvekstregulering(
    val lonnsvekstreguleringId: Long? = null,
    val reguleringsbelop: Double? = null,
    val reguleringsDato: LocalDate? = null
) {
    fun fromDto() =
        Lonnsvekstregulering(reguleringsbelop, reguleringsDato)
}