package no.nav.pensjon.selvbetjeningopptjening.common.domain;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDate;

import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

public class Person {
    private final Log log = LogFactory.getLog(getClass());
    private final Pid pid;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final LocalDate fodselsdato;

    public Person(Pid pid, String fornavn, String mellomnavn, String etternavn, BirthDate birthDate) {
        this.pid = pid;
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        if (birthDate == null) {
            log.warn("No birth dates found for PID " + maskFnr(pid.getPid()));
            fodselsdato = getDefaultFodselsdato(pid);
        } else {
            if (birthDate.isBasedOnYearOnly()) {
                log.info("Birth date set to first day in birth year");
            }
            fodselsdato = birthDate.getValue();
        }

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

    public Log getLog() {
        return log;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    private LocalDate getDefaultFodselsdato(Pid pid) {
        log.info("Deriving birth date directly from PID");
        return pid.getFodselsdato();
    }
}
