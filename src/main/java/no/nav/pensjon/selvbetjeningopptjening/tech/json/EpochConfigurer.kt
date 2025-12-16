package no.nav.pensjon.selvbetjeningopptjening.tech.json

import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.JacksonJsonDecoder
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import java.time.LocalDate

object EpochConfigurer {

    fun jsonEpochCodec(configurer: ClientCodecConfigurer) {
        configurer
            .defaultCodecs()
            .jacksonJsonDecoder(jsonEpochDecoder())
    }

    private fun jsonEpochDecoder() =
        JacksonJsonDecoder(epoch2DateMapper(), MediaType.APPLICATION_JSON)

    /**
     * Required for calls to POPP.
     */
    private fun epoch2DateMapper(): JsonMapper =
        JsonMapper.builder()
            .addModule(dateSerializerModule())
            .build()

    private fun dateSerializerModule() =
        SimpleModule().apply {
            addDeserializer(LocalDate::class.java, LocalDateTimeFromEpochDeserializer())
        }
}