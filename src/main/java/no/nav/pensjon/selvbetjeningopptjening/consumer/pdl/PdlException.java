package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

public class PdlException extends Exception {

    private final String errorCode;

    public PdlException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
