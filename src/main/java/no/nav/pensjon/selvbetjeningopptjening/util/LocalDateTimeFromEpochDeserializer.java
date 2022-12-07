package no.nav.pensjon.selvbetjeningopptjening.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;

import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.ZONE_ID;

public class LocalDateTimeFromEpochDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateTimeFromEpochDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var instant = Instant.ofEpochMilli(parser.readValueAs(Long.class));
        return instant.atZone(ZONE_ID).toLocalDate();
    }
}
