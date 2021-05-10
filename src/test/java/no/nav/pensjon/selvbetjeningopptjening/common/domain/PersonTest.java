package no.nav.pensjon.selvbetjeningopptjening.common.domain;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void should_use_fodselsdato_when_fodselsdato_argument_not_null(){
        LocalDate expectedFodselsdato = LocalDate.now().minusYears(65);
        Person person = new Person(PidGenerator.generatePidAtAge(65), null, null, null, new BirthDate(expectedFodselsdato));

        assertEquals(expectedFodselsdato, person.getFodselsdato());
    }

    @Test
    void should_use_default_fodselsdato_from_pid_when_fodselsdato_argument_is_null(){
        LocalDate expectedFodselsdato = LocalDate.now().minusYears(65);
        Person person = new Person(PidGenerator.generatePid(expectedFodselsdato), null, null, null, null);

        assertEquals(expectedFodselsdato, person.getFodselsdato());
    }

    @Test
    void should_set_names_on_person(){
        String expectedFornavn = "fornavn";
        String expectedMellomnavn = "mellomnavn";
        String expectedEtternavn = "etternavn";

        Person person = new Person(PidGenerator.generatePidAtAge(65), expectedFornavn, expectedMellomnavn, expectedEtternavn, null);

        assertEquals(expectedFornavn, person.getFornavn());
        assertEquals(expectedMellomnavn, person.getMellomnavn());
        assertEquals(expectedEtternavn, person.getEtternavn());
    }

}