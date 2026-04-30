package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto
import java.time.LocalDate

class AfpHistorikkMapperTest : ShouldSpec({

    should("map relevant values") {
        val dto = AfpHistorikkDto(
            virkFom = LocalDate.of(1991, 2, 4),
            virkTom = LocalDate.of(1992, 3, 5)
        )

        val historikk = AfpHistorikkMapper.fromDto(dto)

        with(historikk!!) {
            virkningFomDate shouldBe LocalDate.of(1991, 2, 4)
            startYear shouldBe 1991
            getEndYearOrDefault(defaultYear = { 0 }) shouldBe 1992
        }
    }

    should("use default value when no end year") {
        val dto = AfpHistorikkDto(virkFom = LocalDate.MIN, virkTom = null) // hence no end year

        AfpHistorikkMapper.fromDto(dto)!!
            .getEndYearOrDefault(defaultYear = { 1992 }) shouldBe 1992
    }
})