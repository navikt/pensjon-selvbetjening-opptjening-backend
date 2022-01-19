package no.nav.pensjon.selvbetjeningopptjening.common.domain;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

public class Person {

    private static final Logger log = LoggerFactory.getLogger(Person.class);
    private final Pid pid;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final LocalDate fodselsdato;

    public Person(Pid pid, String fornavn, String mellomnavn, String etternavn, BirthDate fodselsdato) {
        this.pid = pid;
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        this.fodselsdato = getFodselsdato(fodselsdato, pid);
    }

    public Person(Pid pid, BirthDate fodselsdato) {
        this(pid, null, null, null, fodselsdato);
    }

    public Person(Pid pid) {
        this(pid, null);
    }

    public Pid getPid() {
        return pid;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    private static LocalDate getFodselsdato(BirthDate fodselsdato, Pid pid) {
        if (fodselsdato == null) {
            log.warn("No birth dates found for PID {}", maskFnr(pid.getPid()));
            return getDefaultFodselsdato(pid);
        }

        if (fodselsdato.isBasedOnYearOnly()) {
            log.info("Birth date set to first day in birth year");
        }

        return fodselsdato.getValue();
    }

    /**
     * Note: In rare cases this method returns the wrong date, since
     * the first 6 digits of the fødselsnummer is not always the birth date
     */
    private static LocalDate getDefaultFodselsdato(Pid pid) {
        log.info("Deriving birth date directly from PID");
        return pid.getFodselsdato();
    }
}
