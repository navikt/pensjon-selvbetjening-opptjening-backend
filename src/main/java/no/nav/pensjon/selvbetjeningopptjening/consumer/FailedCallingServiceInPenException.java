package no.nav.pensjon.selvbetjeningopptjening.consumer;

public class FailedCallingServiceInPenException extends RuntimeException {
    public FailedCallingServiceInPenException(String message, Throwable cause){
        super(message, cause);
    }
}
