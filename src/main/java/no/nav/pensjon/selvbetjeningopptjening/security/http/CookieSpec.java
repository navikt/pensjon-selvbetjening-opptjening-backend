package no.nav.pensjon.selvbetjeningopptjening.security.http;

public class CookieSpec {

    private final CookieType cookieType;
    private final String cookieValue;

    public CookieSpec(CookieType cookieType, String cookieValue) {
        this.cookieType = cookieType;
        this.cookieValue = cookieValue;
    }

    public CookieType getCookieType() {
        return cookieType;
    }

    public String getCookieValue() {
        return cookieValue;
    }
}
