package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrisTest {

    private static final List<String> unprotectedUris = List.of(
            "/api/status",
            "/internal/alive",
            "/internal/ready",
            "/internal/ping",
            "/internal/prometheus",
            "/internal/selftest",
            "/oauth2/login",
            "/oauth2/legacylogin",
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

    private static final List<String> protectedUris = List.of(
            "",
            "/",
            "/foo",
            "/foo.bar",
            "/foo/",
            "/api",
            "/api/",
            "/api/forstegangssoknad/initiate",
            "/api/forstegangssoknad/save",
            "/api/forstegangssoknad/status",
            "/api/forstegangssoknad/send",
            "/api/forstegangssoknad/kvittering",
            "/api/forstegangssoknad/delete",
            "/api/forstegangssoknad/epost",
            "/api/forstegangssoknad/arbeidsgiver");

    @Test
    void isProtected_returns_true_for_protected_uris() {
        protectedUris.forEach(uri -> assertTrue(Uris.isProtected(uri)));
    }

    @Test
    void isProtected_returns_false_for_unprotected_uris() {
        unprotectedUris.forEach(uri -> assertFalse(Uris.isProtected(uri)));
    }

    @Test
    void isUnprotected_returns_true_for_unprotected_uris() {
        unprotectedUris.forEach(uri -> assertTrue(Uris.isUnprotected(uri)));
    }

    @Test
    void isUnprotected_returns_false_for_protected_uris() {
        protectedUris.forEach(uri -> assertFalse(Uris.isUnprotected(uri)));
    }
}
