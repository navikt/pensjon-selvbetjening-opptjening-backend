package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class PidValidationException extends IllegalArgumentException {
    public PidValidationException(String errorDetail) {
        super("Pid validation failed: " + errorDetail);
    }
}
