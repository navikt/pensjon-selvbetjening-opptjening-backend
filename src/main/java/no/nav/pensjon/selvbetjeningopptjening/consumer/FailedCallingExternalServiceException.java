package no.nav.pensjon.selvbetjeningopptjening.consumer;

public class FailedCallingExternalServiceException extends RuntimeException {

    public FailedCallingExternalServiceException(String serviceProvider, String serviceIdentifier, String detailMessage, Throwable cause) {
        super("Error when calling the external service " + serviceIdentifier + " in " + serviceProvider + ". " + detailMessage, cause);
    }

    public FailedCallingExternalServiceException(String serviceProvider, String detailMessage) {
        super("Error when calling the external service " + serviceProvider + ". " + detailMessage);
    }
}
