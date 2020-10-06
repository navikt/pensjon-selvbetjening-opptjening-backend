package no.nav.pensjon.selvbetjeningopptjening.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleStringExtractorTest {

    @Test
    void extract_returns_value_given_in_constructor() {
        var extractor = new SimpleStringExtractor("foo");
        String actual = extractor.extract();
        assertEquals("foo", actual);
    }
}
