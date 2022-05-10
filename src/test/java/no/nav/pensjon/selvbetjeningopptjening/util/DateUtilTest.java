package no.nav.pensjon.selvbetjeningopptjening.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void firstDayOf() {
        assertEquals(LocalDate.of(2022, 1, 1), DateUtil.firstDayOf(2022));
    }

    @Test
    void reguleringDayOf() {
        assertEquals(LocalDate.of(2022, 5, 1), DateUtil.reguleringDayOf(2022));
    }

    @Test
    void lastDayOf() {
        assertEquals(LocalDate.of(2022, 12, 31), DateUtil.lastDayOf(2022));
    }

    @Test
    void isDateInPeriod() {
        assertTrue(DateUtil.isDateInPeriod(
                LocalDate.of(2022, 6, 15),
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 12, 31)));

        assertFalse(DateUtil.isDateInPeriod(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 6, 15),
                LocalDate.of(2022, 12, 31)));
    }

    @Test
    void getDaysInFebruary() {
        assertEquals(28, DateUtil.getDaysInFebruary(1900));
        assertEquals(29, DateUtil.getDaysInFebruary(2000));
        assertEquals(29, DateUtil.getDaysInFebruary(2004));
        assertEquals(28, DateUtil.getDaysInFebruary(2022));
    }

    @Test
    void isDayOfMonth() {
        assertFalse(DateUtil.isDayOfMonth(32));
        assertTrue(DateUtil.isDayOfMonth(31));
    }

    @Test
    void isMonth() {
        assertFalse(DateUtil.isMonth(13));
        assertTrue(DateUtil.isMonth(12));
    }

    @Test
    void getDaysInMonth() {
        assertEquals(31, DateUtil.getDaysInMonth(1, 2022));
        assertEquals(28, DateUtil.getDaysInMonth(2, 2022));
        assertEquals(29, DateUtil.getDaysInMonth(2, 2020));
        assertEquals(30, DateUtil.getDaysInMonth(4, 2022));
    }
}
