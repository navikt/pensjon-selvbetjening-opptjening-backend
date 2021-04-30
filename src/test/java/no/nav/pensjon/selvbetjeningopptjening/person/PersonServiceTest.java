package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        when(pdlConsumer.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4)))
                .thenReturn(new Person(
                        PID,
                        null,
                        null,
                        null,
                        new BirthDate(LocalDate.of(1982, 3, 4))));

        LocalDate birthDate = personService.getPerson(PID, LoginSecurityLevel.LEVEL4).getFodselsdato();

        assertEquals(LocalDate.of(1982, 3, 4), birthDate);
    }

//    @Test
//    void when_pdl_returns_multipleBirthDates_then_getBirthDate_shall_use_first_birthDate() throws PdlException {
//        when(pdlConsumer.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4)))
//                .thenReturn(List.of(
//                        new BirthDate(LocalDate.of(1982, 3, 4)),
//                        new BirthDate(LocalDate.of(1982, 3, 3)),
//                        new BirthDate(LocalDate.of(1982, 3, 5))));
//
//        LocalDate birthDate = personService.getPerson(PID, LoginSecurityLevel.LEVEL4);
//
//        assertEquals(LocalDate.of(1982, 3, 4), birthDate);
//    }
//
//    @Test
//    void when_pdl_returns_noBirthDates_then_getBirthDate_shall_use_birthDate_from_Pid() throws PdlException {
//        when(pdlConsumer.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4)))
//                .thenReturn(emptyList());
//
//        LocalDate birthDate = personService.getPerson(PID, LoginSecurityLevel.LEVEL4);
//
//        assertEquals(BIRTH_DATE_FROM_PID, birthDate);
//    }
//
//    @Test
//    void when_pdlCall_unauthorized_then_getBirthDate_shall_use_birthDate_from_Pid() throws PdlException {
//        when(pdlConsumer.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4)))
//                .thenThrow(new PdlException("message", "unauthorized"));
//
//        LocalDate birthDate = personService.getPerson(PID, LoginSecurityLevel.LEVEL4);
//
//        assertEquals(BIRTH_DATE_FROM_PID, birthDate);
//    }
//
//    @Test
//    void when_pdlCall_fails_then_getBirthDate_shall_use_birthDate_from_Pid() throws PdlException {
//        when(pdlConsumer.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4)))
//                .thenThrow(new FailedCallingExternalServiceException("", ""));
//
//        LocalDate birthDate = personService.getPerson(PID, LoginSecurityLevel.LEVEL4);
//
//        assertEquals(BIRTH_DATE_FROM_PID, birthDate);
//    }
}
