package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UttaksgradTest {

    @Test
    void test_that_getters_return_expected_values() {
        var uttaksgrad = new Uttaksgrad(
                1L,
                2,
                LocalDate.of(1991, 1, 1),
                LocalDate.of(1992, 2, 2));

        assertEquals(1L, uttaksgrad.getVedtakId());
        assertEquals(2, uttaksgrad.getUttaksgrad());
        assertEquals(LocalDate.of(1991, 1, 1), uttaksgrad.getFomDate());
        assertEquals(LocalDate.of(1992, 2, 2), uttaksgrad.getTomDate());
    }

    @Test
    void coversYear_is_true_when_uttaksgrad_starts_before_given_year_and_has_no_end() {
        Uttaksgrad uttaksgrad = uttaksgrad(1990, null);
        assertTrue(uttaksgrad.coversYear(1991));
    }

    @Test
    void coversYear_is_true_when_uttaksgrad_starts_given_year_and_has_no_end() {
        Uttaksgrad uttaksgrad = uttaksgrad(1991, null);
        assertTrue(uttaksgrad.coversYear(1991));
    }

    @Test
    void coversYear_is_false_when_uttaksgrad_starts_after_given_year() {
        Uttaksgrad uttaksgrad = uttaksgrad(1992, null);
        assertFalse(uttaksgrad.coversYear(1991));
    }

    @Test
    void coversYear_is_true_when_uttaksgrad_starts_and_ends_at_given_year() {
        Uttaksgrad uttaksgrad = uttaksgrad(1992, 1992);
        assertTrue(uttaksgrad.coversYear(1992));
    }

    @Test
    void coversYear_is_false_when_uttaksgrad_ends_before_given_year() {
        Uttaksgrad uttaksgrad = uttaksgrad(1992, 1999);
        assertFalse(uttaksgrad.coversYear(2000));
    }

    @Test
    void isGradert_is_true_when_uttaksgradPercent_is_between_0_and_100() {
        assertTrue(uttaksgrad(1).isGradert());
        assertTrue(uttaksgrad(50).isGradert());
        assertTrue(uttaksgrad(99).isGradert());
    }

    @Test
    void isGradert_is_false_when_uttaksgradPercent_is_0_or_less() {
        assertFalse(uttaksgrad(0).isGradert());
        assertFalse(uttaksgrad(-100).isGradert());
    }

    @Test
    void isGradert_is_false_when_uttaksgradPercent_is_100_or_more() {
        assertFalse(uttaksgrad(100).isGradert());
        assertFalse(uttaksgrad(200).isGradert());
    }

    private static Uttaksgrad uttaksgrad(int fomYear, Integer tomYear) {
        return new Uttaksgrad(
                1L,
                2,
                LocalDate.of(fomYear, 1, 1),
                tomYear == null ? null : LocalDate.of(tomYear, 12, 31));
    }

    private static Uttaksgrad uttaksgrad(int percent) {
        return new Uttaksgrad(
                1L,
                percent,
                LocalDate.of(1991, 1, 1),
                LocalDate.of(1991, 12, 31));
    }
}
