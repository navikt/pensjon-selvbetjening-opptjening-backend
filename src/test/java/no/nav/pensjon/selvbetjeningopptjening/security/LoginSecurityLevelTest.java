package no.nav.pensjon.selvbetjeningopptjening.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginSecurityLevelTest {

    @Test
    void findByAcrValue_finds_value_from_acrValue() {
        assertEquals(LoginSecurityLevel.LEVEL3, LoginSecurityLevel.findByAcrValue("Level3"));
        assertEquals(LoginSecurityLevel.LEVEL4, LoginSecurityLevel.findByAcrValue("Level4"));
    }

    @Test
    void findByAcrValue_returns_noneValue_when_no_match() {
        assertEquals(LoginSecurityLevel.NONE, LoginSecurityLevel.findByAcrValue("invalid"));
        assertEquals(LoginSecurityLevel.NONE, LoginSecurityLevel.findByAcrValue("N/A"));
        assertEquals(LoginSecurityLevel.NONE, LoginSecurityLevel.findByAcrValue(""));
        assertEquals(LoginSecurityLevel.NONE, LoginSecurityLevel.findByAcrValue(null));
    }

    @Test
    void findByAcrValue_is_caseSensitive() {
        assertEquals(LoginSecurityLevel.NONE, LoginSecurityLevel.findByAcrValue("level3"));
        assertEquals(LoginSecurityLevel.NONE, LoginSecurityLevel.findByAcrValue("LEVEL4"));
    }
}
