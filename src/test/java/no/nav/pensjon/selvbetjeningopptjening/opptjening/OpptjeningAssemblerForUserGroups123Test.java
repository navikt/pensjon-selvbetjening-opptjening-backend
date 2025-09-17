package no.nav.pensjon.selvbetjeningopptjening.opptjening;
/*
import static junit.framework.TestCase.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;

@ExtendWith(SpringExtension.class)
class OpptjeningAssemblerForUserGroups123Test {
    @Mock
    private UttaksgradGetter uttaksgradGetter;

    private OpptjeningAssemblerForUserGroups123 assembler;

    @BeforeEach
    void setup() {
        assembler = new OpptjeningAssemblerForUserGroups123(uttaksgradGetter);
    }

    @Test
    void should_return_andel_RegelverkBeholdning_equal_to_0_when_usergroup5() {
        try (RequestContext ignored = RequestContextCreator.createForExternal()) {
            OpptjeningResponse response = assembler.createResponse(new Person(
                            PidGenerator.generatePidAtAge(50),
                            null,
                            null,
                            null,
                            null),
                    emptyOpptjeningBasis());
            assertEquals(response.getAndelPensjonBasertPaBeholdning(), 0);
        }
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
*/
