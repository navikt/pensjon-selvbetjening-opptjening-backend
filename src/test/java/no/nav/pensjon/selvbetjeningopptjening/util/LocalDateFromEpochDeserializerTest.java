package no.nav.pensjon.selvbetjeningopptjening.util;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LocalDateFromEpochDeserializerTest {

    @Mock
    JsonParser parser;

    @Test
    void deserialize_gets_localDate_from_epoch() throws IOException {
        when(parser.readValueAs(Long.class)).thenReturn(757378900000L);
        LocalDate date = new LocalDateTimeFromEpochDeserializer().deserialize(parser, null);
        assertEquals(LocalDate.of(1994, 1, 1), date);
    }
}
