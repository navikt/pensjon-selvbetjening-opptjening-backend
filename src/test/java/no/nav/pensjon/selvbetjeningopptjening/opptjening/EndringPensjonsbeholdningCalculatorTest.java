package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;

class EndringPensjonsbeholdningCalculatorTest {

    @Test
    void when_empty_input_then_calculateEndringPensjonsbeholdning_returns_null_list() {
        List<EndringPensjonsopptjeningDto> dtos =
                new EndringPensjonsbeholdningCalculator().calculateEndringPensjonsbeholdning(2020, new ArrayList<>(), new ArrayList<>());

        assertNull(dtos);
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_GivenYear_then_calculateEndringPensjonsbeholdning_returns_2_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 4, 1));
        beholdning.setBelop(1d);
        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                new EndringPensjonsbeholdningCalculator().calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(2, dtos.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1JanGivenYear_then_calculateEndringPensjonsbeholdning_returns_3_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                new EndringPensjonsbeholdningCalculator().calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(3, dtos.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_then_calculateEndringPensjonsbeholdning_returns_4_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setTomDato(LocalDate.of(2020,12,31));
        beholdning.setBelop(1d);
        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                new EndringPensjonsbeholdningCalculator().calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(4, dtos.size());
    }
}
