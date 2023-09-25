package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import java.util.List;

public class Uris {

    private static final List<String> unprotectedUris = List.of(
            "/api/status",
            "/internal/alive",
            "/internal/ready",
            "/internal/ping",
            "/internal/prometheus",
            "/internal/selftest",
            "/oauth2/login",
            "/oauth2/callback",
            "/api/mocklogin/04925398980", // deprecated
            "/oauth2/external/callback", // deprecated
            "/oauth2/external/login", // deprecated
            "/oauth2/external/refresh-token", // deprecated
            "/oauth2/internal/callback", // deprecated
            "/oauth2/internal/login", // deprecated
            "/oauth2/internal/refresh-token", // deprecated
            "/oauth2/refresh-token",
            "/oauth2-ad/login",
            "/oauth2-ad/callback",
            "/oauth2-ad/refresh-token",
            "/logout",
            "/favicon.ico");

    public static boolean isProtected(String uri) {
        return !isUnprotected(uri);
    }

    public static boolean isUnprotected(String uri) {
        return unprotectedUris.contains(uri);
    }
}
