package no.nav.pensjon.selvbetjeningopptjening.util;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Periode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PeriodeUtilTest {

    @Test
    void test_that_sortPerioderByFomDate_sorts_by_fom_date() {
        List<TestPeriode> actual = PeriodeUtil.sortPerioderByFomDate(
                List.of(
                        new TestPeriode(
                                LocalDate.of(2020, 1, 1),
                                LocalDate.of(2020, 1, 31)),
                        new TestPeriode(
                                LocalDate.of(2019, 1, 1),
                                LocalDate.of(2019, 1, 31))));

        assertEquals(2019, actual.get(0).getFomDato().getYear());
        assertEquals(2020, actual.get(1).getFomDato().getYear());
    }

    @Test
    void test_that_sortPerioderByFomDate_disregards_tom_date() {
        List<TestPeriode> actual = PeriodeUtil.sortPerioderByFomDate(
                List.of(
                        new TestPeriode(
                                LocalDate.of(2020, 1, 1),
                                LocalDate.of(2020, 1, 31)),
                        new TestPeriode(
                                LocalDate.of(2019, 1, 1),
                                LocalDate.of(2020, 2, 1))));

        assertEquals(2019, actual.get(0).getFomDato().getYear());
        assertEquals(2020, actual.get(1).getFomDato().getYear());
    }

    @Test
    void when_periods_equal_then_isPeriodeWithinInterval_returns_false() {
        boolean actual = PeriodeUtil.isPeriodeWithinInterval(
                new TestPeriode(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 1, 31)),
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 31));

        assertFalse(actual);
    }

    @Test
    void when_period_outside_interval_on_both_sides_then_isPeriodeWithinInterval_returns_false() {
        boolean actual = PeriodeUtil.isPeriodeWithinInterval(
                new TestPeriode(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 1, 31)),
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 1, 30));

        assertFalse(actual);
    }

    @Test
    void when_period_outside_interval_on_start_side_then_isPeriodeWithinInterval_returns_false() {
        boolean actual = PeriodeUtil.isPeriodeWithinInterval(
                new TestPeriode(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 1, 30)),
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 1, 31));

        assertFalse(actual);
    }

    @Test
    void when_period_outside_interval_on_end_side_then_isPeriodeWithinInterval_returns_false() {
        boolean actual = PeriodeUtil.isPeriodeWithinInterval(
                new TestPeriode(
                        LocalDate.of(2020, 1, 2),
                        LocalDate.of(2020, 1, 31)),
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 30));

        assertFalse(actual);
    }

    @Test
    void when_period_strictly_within_inteval_then_isPeriodeWithinInterval_returns_true() {
        boolean actual = PeriodeUtil.isPeriodeWithinInterval(
                new TestPeriode(
                        LocalDate.of(2020, 1, 2),
                        LocalDate.of(2020, 1, 30)),
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 31));

        assertTrue(actual);
    }

    private static class TestPeriode implements Periode {

        private final LocalDate start;
        private final LocalDate end;

        private TestPeriode(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public LocalDate getFomDato() {
            return start;
        }

        @Override
        public LocalDate getTomDato() {
            return end;
        }
    }
}
