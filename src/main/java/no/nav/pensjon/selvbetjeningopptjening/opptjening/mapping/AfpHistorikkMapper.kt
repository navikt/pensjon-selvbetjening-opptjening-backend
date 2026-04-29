package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk

object AfpHistorikkMapper {

    fun fromDto(dto: AfpHistorikkDto?): AfpHistorikk? =
        dto?.let {
            AfpHistorikk(
                virkningFomDate = it.virkFom,
                virkningTomDate = it.virkTom
            )
        }
}