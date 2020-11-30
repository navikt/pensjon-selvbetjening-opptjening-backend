package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class OpptjeningAssemblerTest {

    @Mock
    UttaksgradGetter uttaksgradGetter;

    @Test
    void test_that_removeFutureOpptjening_removes_entry_for_given_year() {
        Map<Integer, Opptjening> opptjeningerByYear = new HashMap<>();
        opptjeningerByYear.put(2018, opptjening());
        opptjeningerByYear.put(2019, opptjening());
        opptjeningerByYear.put(2020, opptjening());

        new TestOpptjeningAssembler(uttaksgradGetter).removeFutureOpptjening(opptjeningerByYear, 2019);

        assertEquals(2, opptjeningerByYear.size());
    }

    private static Opptjening opptjening() {
        return new Opptjening(1L, 1.1D);
    }

    private static class TestOpptjeningAssembler extends OpptjeningAssembler {

        TestOpptjeningAssembler(UttaksgradGetter uttaksgradGetter) {
            super(uttaksgradGetter);
        }
    }
}
