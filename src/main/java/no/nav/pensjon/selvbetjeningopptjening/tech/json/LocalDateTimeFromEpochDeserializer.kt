package no.nav.pensjon.selvbetjeningopptjening.tech.json

import no.nav.pensjon.selvbetjeningopptjening.util.DateUtil
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.deser.std.StdDeserializer
import java.time.Instant
import java.time.LocalDate

/**
 * Serializes epoch milliseconds as java.time.LocalDate.
 */
class LocalDateTimeFromEpochDeserializer() : StdDeserializer<LocalDate>(Long::class.java) {

    override fun deserialize(parser: JsonParser, provider: DeserializationContext?): LocalDate =
        Instant.ofEpochMilli(parser.readValueAs(Long::class.java))
            .atZone(DateUtil.ZONE_ID)
            .toLocalDate()
}