package no.nav.pensjon.selvbetjeningopptjening.security.masking;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaskerTest {

    @Test
    void maskFnr_masks_other_fnr_digits_than_birthDate() {
        assertEquals("123456*****", Masker.maskFnr("12345678901"));
        assertEquals("049253*****", Masker.maskFnr(new Pid("04925398980")));
    }

    @Test
    void maskFnr_masks_all_digits_in_nonFnr() {
        assertEquals("****** (length 10)", Masker.maskFnr("1234567890"));
    }

    @Test
    void maskFnr_handles_empty_string() {
        assertEquals("****** (length 0)", Masker.maskFnr(""));
    }

    @Test
    void maskFnr_returns_textNull_for_valueNull() {
        assertEquals("null", Masker.maskFnr((Pid) null));
        assertEquals("null", Masker.maskFnr((String) null));
    }
}
