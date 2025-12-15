package no.nav.pensjon.selvbetjeningopptjening.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.nav.pensjon.selvbetjeningopptjening.util.LocalDateTimeFromEpochDeserializer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.LocalDate;

class JsonEpochExchangeStrategies {

    static ExchangeStrategies build() {
        return ExchangeStrategies.builder()
                .codecs(JsonEpochExchangeStrategies::jsonEpochCodec)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    private static void jsonEpochCodec(ClientCodecConfigurer configurer) {
        configurer
                .defaultCodecs()
                .jackson2JsonDecoder(jsonEpochDecoder());
    }

    private static Jackson2JsonDecoder jsonEpochDecoder() {
        return new Jackson2JsonDecoder(epoch2DateMapper(), MediaType.APPLICATION_JSON);
    }

    private static ObjectMapper epoch2DateMapper() {
        var mapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new LocalDateTimeFromEpochDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
