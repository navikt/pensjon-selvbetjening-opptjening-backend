package no.nav.pensjon.selvbetjeningopptjening.tech.json

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import tools.jackson.core.JsonParser
import java.time.LocalDate

class LocalDateTimeFromEpochDeserializerTest : ShouldSpec( {

    should("get date from epoch")  {
        val parser = mockk<JsonParser>().apply { every { readValueAs(Long::class.java) } returns 757378900000L }

        LocalDateTimeFromEpochDeserializer().deserialize(parser, null) shouldBe
                LocalDate.of(1994, 1, 1)
    }
})
