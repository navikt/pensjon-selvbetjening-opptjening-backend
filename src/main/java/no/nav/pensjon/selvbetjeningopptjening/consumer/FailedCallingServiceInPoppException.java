package no.nav.pensjon.selvbetjeningopptjening.consumer;

public class FailedCallingServiceInPoppException extends RuntimeException {
    public FailedCallingServiceInPoppException(String message, Throwable cause){
        super(message, cause);
    }
}
