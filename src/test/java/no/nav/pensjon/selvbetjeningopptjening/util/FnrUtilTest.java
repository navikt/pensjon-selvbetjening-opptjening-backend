package no.nav.pensjon.selvbetjeningopptjening.util;

import static org.junit.jupiter.api.Assertions.*;

import static no.nav.pensjon.selvbetjeningopptjening.util.FnrUtil.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FnrUtilTest {

    @Test
    public void when_Dnr_in_input_then_getFodselsdatoForFnr_returns_valid_Date(){
        String fnr = "42126531074";

        assertEquals(LocalDate.of(1965, 12, 2), getFodselsdatoForFnr(fnr));
    }

    @Test
    public void when_Bostnr_in_input_then_getFodselsdatoForFnr_returns_valid_Date(){
        String fnr = "12226531074";

        assertEquals(LocalDate.of(1965, 2, 12), getFodselsdatoForFnr(fnr));
    }

    @Test
    public void when_empty_input_then_getFodselsdatoForFnr_returns_valid_Date(){
        String fnr = "";

        assertNull(getFodselsdatoForFnr(fnr));
    }

    @Test
    public void when_input_with_valid_individnr_then_getFodselsdatoForFnr_returns_valid_Year(){
        String fnr = "12026561074";

        assertEquals(LocalDate.of(1865, 2, 12), getFodselsdatoForFnr(fnr));
    }

    @Test
    public void when_input_with_invalid_individnr_then_getFodselsdatoForFnr_returns_valid_Year(){
        String fnr = "12025061074";

        assertNull(getFodselsdatoForFnr(fnr));
    }
}
