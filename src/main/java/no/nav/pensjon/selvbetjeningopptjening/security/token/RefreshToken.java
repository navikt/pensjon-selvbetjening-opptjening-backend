package no.nav.pensjon.selvbetjeningopptjening.security.token;

public class RefreshToken {

    private final String value;

    public RefreshToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
