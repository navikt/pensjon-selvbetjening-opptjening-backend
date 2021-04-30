package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static junit.framework.TestCase.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;

@ExtendWith(SpringExtension.class)
class OpptjeningAssemblerForUserGroup5Test {
    @Mock
    private UttaksgradGetter uttaksgradGetter;

    private OpptjeningAssemblerForUserGroup5 assembler;

    @BeforeEach
    void setup() {
        assembler = new OpptjeningAssemblerForUserGroup5(uttaksgradGetter);
    }

    @Test
    void should_return_andel_RegelverkBeholdning_equal_to_10_when_usergroup5() {
        OpptjeningResponse response = assembler.createResponse(new Person(
                        PidGenerator.generatePidAtAge(50),
                        null,
                        null,
                        null,
                        null),
                emptyOpptjeningBasis());
        assertEquals(response.getAndelPensjonBasertPaBeholdning(), 10);
    }

    private OpptjeningBasis emptyOpptjeningBasis() {
        return new OpptjeningBasis(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new AfpHistorikk(LocalDate.now(), null),
                new UforeHistorikk(new ArrayList<>())
        );
    }
}