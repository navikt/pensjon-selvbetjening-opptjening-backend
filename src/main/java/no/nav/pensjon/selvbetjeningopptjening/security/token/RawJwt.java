package no.nav.pensjon.selvbetjeningopptjening.security.token;

public class RawJwt {

    private final String value;

    public RawJwt(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
