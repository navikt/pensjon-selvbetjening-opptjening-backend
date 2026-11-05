package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1.acl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad

@JsonInclude(NON_NULL)
data class UttaksgradV1(
    @field:Schema(description = "Uttaksgrad angitt i prosent")
    @field:NotNull
    val grad: Int,

    @field:Schema(description = "Hvilken kalendermåned (1..12) uttaket med denne graden startet (fra og med) - kun angitt for året da uttaket startet")
    val kalendermaanedFom: Int?
) {
    companion object {
        fun transferable(source: Uttaksgrad, aar: Int) =
            UttaksgradV1(
                grad = source.grad,
                kalendermaanedFom = source.fom.let { if (it.year == aar) it.monthValue else null }
            )
    }
}