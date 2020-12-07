package no.nav.pensjon.selvbetjeningopptjening.security.http;

public enum CookieType {

    INTERNAL_USER_ACCESS_TOKEN("iu-acctoken", "/", true, true),
    INTERNAL_USER_ID_TOKEN("iu-idtoken", "/", true, true),
    EXTERNAL_USER_ACCESS_TOKEN("xu-acctoken", "/", true, true),
    EXTERNAL_USER_ID_TOKEN("xu-idtoken", "/", true, true),
    REFRESH_TOKEN("refresh-token", "/", true, true);

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isSecure() {
        return secure;
    }

    private final String name;
    private final String path;
    private final boolean httpOnly;
    private final boolean secure;

    CookieType(String name, String path, boolean httpOnly, boolean secure) {
        this.name = name;
        this.path = path;
        this.httpOnly = httpOnly;
        this.secure = secure;
    }
}
