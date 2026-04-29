package no.nav.pensjon.selvbetjeningopptjening.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class AfpHistorikkDto (
    val virkFom: LocalDate,
    val virkTom: LocalDate?
)