package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.person.pid.PidValidator;

import static java.lang.String.format;

/**
 * Represents a personal identification number (PID).
 * The PID types supported are: Fødselsnummer (FNR), D-nummer (DNR) and NPID/BOST-nummer (BNR).
 * BOST-nummer is an historical NAV-internal PID type.
 */
public class Pid {

    private final String value;

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
