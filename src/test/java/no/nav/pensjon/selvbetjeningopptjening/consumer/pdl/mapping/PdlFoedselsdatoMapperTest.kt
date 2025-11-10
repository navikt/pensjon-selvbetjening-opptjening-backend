package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedselsdato
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2
import java.time.LocalDate

class PdlFoedselsdatoMapperTest : ShouldSpec({

    should("map to domain objects") {
        val dto1 = birth(1981, 2, 3)
        val dto2 = birth(1982, 12, 31).apply { foedselsaar = 1983 }
        val dto3 = Foedselsdato().apply { foedselsaar = 1984 }

        val result: List<Foedselsdato2> = PdlFoedselsdatoMapper.fromDtos(listOf(dto1, dto2, dto3))

        result shouldHaveSize 3
        result[0] shouldBe Foedselsdato2(LocalDate.of(1981, 2, 3), false)
        result[1] shouldBe Foedselsdato2(LocalDate.of(1982, 12, 31), false)
        result[2] shouldBe Foedselsdato2(LocalDate.of(1984, 1, 1), true)
    }

    should("ignore null values") {
        val dtos: List<Foedselsdato?> = listOf(null, birth(1981, 2, 3))

        val result: List<Foedselsdato2> = PdlFoedselsdatoMapper.fromDtos(list = dtos)

        result shouldHaveSize 1
        result[0] shouldBe Foedselsdato2(LocalDate.of(1981, 2, 3), false)
    }

    should("map null list to empty list") {
        PdlFoedselsdatoMapper.fromDtos(list = null) shouldHaveSize 0
    }
})

private fun birth(year: Int, month: Int, dayOfMonth: Int) =
    Foedselsdato().apply { foedselsdato = LocalDate.of(year, month, dayOfMonth) }