package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;

@ExtendWith(SpringExtension.class)
class OpptjeningAssemblerForUserGroup4Test {
    @Mock
    private UttaksgradGetter uttaksgradGetter;

    private OpptjeningAssemblerForUserGroup4 assembler;

    @BeforeEach
    void setup() {
        assembler = new OpptjeningAssemblerForUserGroup4(uttaksgradGetter);
    }

    @Test
    void should_increase_andel_regelverk_beholdning_with_1_for_each_year_in_usergroup4() {
        Map<Integer, Integer> expectedValuesForEachYear = Stream.of(new Integer[][]{
                {1954, 1},
                {1955, 2},
                {1956, 3},
                {1957, 4},
                {1958, 5},
                {1959, 6},
                {1960, 7},
                {1961, 8},
                {1962, 9}
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (Integer) data[1]));

        expectedValuesForEachYear.keySet().forEach(year -> {
            LocalDate fodselsdato = LocalDate.of(year, 5, 5);
            OpptjeningResponse response = assembler.createResponse(new Person(
                            PidGenerator.generatePid(fodselsdato),
                            null,
                            null,
                            null,
                            null),
                    emptyOpptjeningBasis());
            int expectedAndelRegelverkBeholdning = expectedValuesForEachYear.get(year);
            assertThat("For " + year + " andelNyttRegelverk should be " + expectedAndelRegelverkBeholdning, response.getAndelPensjonBasertPaBeholdning(), is(expectedAndelRegelverkBeholdning));
        });
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