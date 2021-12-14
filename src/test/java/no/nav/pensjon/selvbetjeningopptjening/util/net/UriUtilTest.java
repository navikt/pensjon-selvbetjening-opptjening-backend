package no.nav.pensjon.selvbetjeningopptjening.util.net;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class UriUtilTest {

    @Test
    void uriFrom_url_with_port_produces_uri() throws URISyntaxException {
        URI uri = UriUtil.uriFrom("https://www.example.net:80");
        assertEquals("www.example.net", uri.getHost());
        assertEquals(80, uri.getPort());
    }

    @Test
    void uriFrom_url_without_port_causes_URISyntaxException() {
        var exception = assertThrows(URISyntaxException.class, () -> UriUtil.uriFrom("https://www.example.net"));
        assertEquals("No URI port specified: https://www.example.net", exception.getMessage());
    }

    @Test
    void uriFrom_nonUri_causes_URISyntaxException() {
        var exception = assertThrows(URISyntaxException.class, () -> UriUtil.uriFrom("invalid"));
        assertEquals("No URI port specified: invalid", exception.getMessage());
    }
}
