package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.mock.MockLoginInfoGetter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleLoginInfoGetterTest {

    @Test
    void getLoginInfo_shall_contain_supplied_fnr() {
        String fnr = new MockLoginInfoGetter(TestFnrs.NORMAL).getLoginInfo().getPid().getPid();
        assertEquals(TestFnrs.NORMAL, fnr);
    }
}
