package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

/**
 * Used for problems related to Security Token Service (STS) access.
 */
public class StsException extends Exception {

    StsException(String message, Throwable cause) {
        super(message, cause);
    }
}
