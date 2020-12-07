package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

public class MissingTokenException extends RuntimeException {

    MissingTokenException(String message) {
        super(message);
    }
}
