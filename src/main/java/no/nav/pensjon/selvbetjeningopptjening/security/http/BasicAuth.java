package no.nav.pensjon.selvbetjeningopptjening.security.http;

import java.util.Base64;

import static java.util.Objects.requireNonNull;

public class BasicAuth {

    private static final String PREFIX = "Basic";

    public static String getBasicAuthHeader(String username, String password) {
        String credentials = requireNonNull(username) + ":" + requireNonNull(password);
        return PREFIX + " " + Base64.getEncoder().encodeToString((credentials).getBytes());
    }
}
