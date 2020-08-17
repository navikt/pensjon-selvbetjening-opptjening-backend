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
    void when_beholdning_list_has_one_element_then_calculateEndringPensjonsbeholdningr_returns_3_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                new EndringPensjonsbeholdningCalculator().calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(3, dtos.size());
    }
}
