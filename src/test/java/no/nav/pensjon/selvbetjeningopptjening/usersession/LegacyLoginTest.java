package no.nav.pensjon.selvbetjeningopptjening.usersession;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyLoginTest {

    @Test
    void when_enabled_then_getUrl_returns_url() {
        LegacyLogin login = LegacyLogin.enabled("url1");

        assertTrue(login.isEnabled());
        assertEquals("url1", login.getUrl());
    }

    @Test
    void when_disabled_then_getUrl_returns_emptyString() {
        LegacyLogin login = LegacyLogin.disabled();

        assertFalse(login.isEnabled());
        assertEquals("", login.getUrl());
    }
}
