package no.nav.pensjon.selvbetjeningopptjening.consumer;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

public class PoppUtil {

    private static final int CHECKED_EXCEPTION_HTTP_STATUS = 512;

    public static FailedCallingExternalServiceException handle(RestClientResponseException e, String service) {
        int status = e.getRawStatusCode();

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(POPP, service, "Received 401 UNAUTHORIZED", e);
        }

        if (status == CHECKED_EXCEPTION_HTTP_STATUS && isPersonDoesNotExistMessage(e.getMessage())) {
            return new FailedCallingExternalServiceException(POPP, service, "Person ikke funnet", e);
        }

        if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(POPP, service, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        }

        return new FailedCallingExternalServiceException(POPP, service, "An error occurred in the provider", e);
    }

    private static boolean isPersonDoesNotExistMessage(String exceptionMessage) {
        return exceptionMessage != null && exceptionMessage.contains("PersonDoesNotExistExceptionDto");
    }
}
