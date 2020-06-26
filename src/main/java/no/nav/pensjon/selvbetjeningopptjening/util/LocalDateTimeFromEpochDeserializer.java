package no.nav.pensjon.selvbetjeningopptjening.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LocalDateTimeFromEpochDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateTimeFromEpochDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return Instant.ofEpochMilli(jp.readValueAs(Long.class)).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}