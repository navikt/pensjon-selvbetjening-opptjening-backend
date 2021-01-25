package no.nav.pensjon.selvbetjeningopptjening.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListUtilTest {

    @Test
    void listOf_returns_aggregate_list() {
        List<String> list = List.of("foo", "bar");

        List<String> actual = ListUtil.listOf(list, "baz");

        assertEquals(3, actual.size());
        assertEquals("foo", actual.get(0));
        assertEquals("bar", actual.get(1));
        assertEquals("baz", actual.get(2));
    }
}
