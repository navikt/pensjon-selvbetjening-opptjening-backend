package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.lang.String.format;
import static no.nav.pensjon.selvbetjeningopptjening.person.PidValidator.getDatePart;
import static no.nav.pensjon.selvbetjeningopptjening.person.PidValidator.isValidPid;

/**
 * Represents a personal identification number (PID).
 * A PID can be: Fødselsnummer (FNR), D-nummer (DNR) or BOST-nummer (BNR).
 */
public class Pid {

    private static final String DATO_PATTERN = "ddMMyyyy";
    private final String value;
    private LocalDate foedselsdato; // lazily evaluated

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
    public LocalDate getFodselsdato() {
        if (foedselsdato != null) {
            return foedselsdato;
        }

        try {
            return foedselsdato = LocalDate.parse(getDatePart(value), DateTimeFormatter.ofPattern(DATO_PATTERN));
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
        if (isValidPid(value, acceptSpecialCircumstances)) {
            return;
        }

        throw new PidValidationException(format("The value '%s' is not a valid personal identification number", value));
    }
}
