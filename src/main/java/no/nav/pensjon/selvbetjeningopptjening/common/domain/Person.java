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

    public Person(Pid pid, String fornavn, String mellomnavn, String etternavn, BirthDate fodselsdato) {
        this.pid = pid;
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        if (fodselsdato == null) {
            log.warn("No birth dates found for PID " + maskFnr(pid.getPid()));
            this.fodselsdato = getDefaultFodselsdato(pid);
        } else {
            if (fodselsdato.isBasedOnYearOnly()) {
                log.info("Birth date set to first day in birth year");
            }
            this.fodselsdato = fodselsdato.getValue();
        }

    }

    public Person(Pid pid, BirthDate fodselsdato){
        this(pid, null, null, null, fodselsdato);
    }

    public Person(Pid pid){
        this(pid, null);
    }

    private LocalDate getDefaultFodselsdato(Pid pid) {
        log.info("Deriving birth date directly from PID");
        return pid.getFodselsdato();
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


}
