package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PersonServiceTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private static final LocalDate BIRTH_DATE_FROM_PID = LocalDate.of(1991, 2, 3);
    private PersonService personService;

    @Mock
    private PdlConsumer pdlConsumer;

    @BeforeEach
    void initialize() {
        personService = new PersonService(pdlConsumer);
    }

    @Test
    void when_pdl_returns_oneBirthDate_then_getBirthDate_shall_use_that_birthDate() throws PdlException {
        when(pdlConsumer.getPerson(any(Pid.class)))
                .thenReturn(new Person(
                        PID,
                        null,
                        null,
                        null,
                        new BirthDate(LocalDate.of(1982, 3, 4))));

        LocalDate birthDate = personService.getPerson(PID).getFodselsdato();

        assertEquals(LocalDate.of(1982, 3, 4), birthDate);
    }



    @Test
    void when_pdlCall_unauthorized_then_getBirthDate_shall_use_birthDate_from_Pid() throws PdlException {
        when(pdlConsumer.getPerson(any(Pid.class)))
                .thenThrow(new PdlException("message", "unauthorized"));

        Person person = personService.getPerson(PID);

        assertEquals(BIRTH_DATE_FROM_PID, person.getFodselsdato());
    }

    @Test
    void when_pdlCall_fails_then_getBirthDate_shall_use_birthDate_from_Pid() throws PdlException {
        when(pdlConsumer.getPerson(any(Pid.class)))
                .thenThrow(new FailedCallingExternalServiceException("", ""));

        Person person = personService.getPerson(PID);

        assertEquals(BIRTH_DATE_FROM_PID, person.getFodselsdato());
    }
}
