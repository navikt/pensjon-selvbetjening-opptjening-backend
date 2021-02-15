package no.nav.pensjon.selvbetjeningopptjening.security.token;

/**
 * Used for problems related to Security Token Service (STS) access.
 */
public class StsException extends Exception {

    public StsException(String message, Throwable cause) {
        super(message, cause);
    }
}
