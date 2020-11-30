package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpptjeningAssemblerTest {

    @Mock
    UttaksgradGetter uttaksgradGetter;

    @Test
    void removeFutureOpptjening() {
        Map<Integer, Opptjening> opptjeningerByYear = new HashMap<>();
        opptjeningerByYear.put(2018, new Opptjening(1L, 1.1D));
        opptjeningerByYear.put(2019, new Opptjening(1L, 1.1D));
        opptjeningerByYear.put(2020, new Opptjening(1L, 1.1D));

        new TestOpptjeningAssembler(uttaksgradGetter).removeFutureOpptjening(opptjeningerByYear, 2019);

        assertEquals(2, opptjeningerByYear.size());
    }

    private static class TestOpptjeningAssembler extends OpptjeningAssembler {

        TestOpptjeningAssembler(UttaksgradGetter uttaksgradGetter) {
            super(uttaksgradGetter);
        }
    }
}
