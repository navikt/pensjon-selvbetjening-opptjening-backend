package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.person.pid.PidValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.lang.String.format;

/**
 * Represents a personal identification number (PID).
 * The PID types supported are: Fødselsnummer (FNR), D-nummer (DNR) and NPID/BOST-nummer (BNR).
 * BOST-nummer is an historical NAV-internal PID type.
 */
public class Pid {

    private static final String DATO_PATTERN = "ddMMyyyy";
    private final String value;
    private LocalDate foedselsdato;

    public Pid(String value) throws PidValidationException {
        this(value, false);
    }

    public Pid(String value, boolean acceptSpecialCircumstances) throws PidValidationException {
        this.value = value;
        validate(acceptSpecialCircumstances);
    }

    public String getPid() {
        return value;
    }

    //TODO This is not a reliable way of getting fødselsdato,
    // since the date part of the PID is not always the fødselsdato
    @Deprecated
    public LocalDate getFodselsdato() {
        if (foedselsdato != null) {
            return foedselsdato;
        }

        try {
            return foedselsdato = LocalDate.parse(PidValidator.INSTANCE.datoPart(value), DateTimeFormatter.ofPattern(DATO_PATTERN));
        } catch (DateTimeParseException e) {
            throw new PidValidationException(format("The value '%s' does not contain dato", value));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pid pid && value.equals(pid.value);
    }

    @Override
    public String toString() {
        return value;
    }

    private void validate(boolean acceptSpecialCircumstances) throws PidValidationException {
        if (PidValidator.INSTANCE.isValidPid(value, acceptSpecialCircumstances)) {
            return;
        }

        throw new PidValidationException(format("The value '%s' is not a valid personal identification number", value));
    }
}
