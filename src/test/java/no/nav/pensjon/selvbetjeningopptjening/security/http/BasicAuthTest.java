package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthTest {

    @Test
    void getBasicAuthHeader_returns_encodedValue_with_prefix() {
        String header = BasicAuth.getBasicAuthHeader("user", "secret");
        assertEquals("Basic dXNlcjpzZWNyZXQ=", header);
    }
}
