package no.nav.pensjon.selvbetjeningopptjening.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LocalDateTimeFromEpochDeserializer extends StdDeserializer<LocalDate> {

    private static final String TIME_ZONE = "UTC+2"; // Norway daylight saving time

    public LocalDateTimeFromEpochDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var instant = Instant.ofEpochMilli(parser.readValueAs(Long.class));
        return instant.atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
    }
}
