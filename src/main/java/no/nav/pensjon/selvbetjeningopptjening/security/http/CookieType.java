package no.nav.pensjon.selvbetjeningopptjening.security.http;

/**
 * Cookie name explanation:
 * iu/xu = NAV internal/external user
 * acc = access
 * nav = NAV
 * obo = on behalf of
 */
public enum CookieType {

    INTERNAL_USER_ACCESS_TOKEN("iu-acctoken", "/", true, true),
    EXTERNAL_USER_ACCESS_TOKEN("xu-acctoken", "/", true, true),
    REFRESH_TOKEN("refresh-token", "/", true, true), // since no DB, store refresh token in cookie
    ON_BEHALF_OF_PID("nav-obo", "/", true, true);

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
