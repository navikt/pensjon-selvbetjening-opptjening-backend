package no.nav.pensjon.selvbetjeningopptjening.security;

import static java.util.Arrays.stream;

public enum LoginSecurityLevel {

    NONE("N/A"), // N/A = Not applicable
    INTERNAL("N/A"), // No "acr" claim in JWT for internal users
    LEVEL3("Level3"),
    LEVEL4("Level4");

    // ACR = Authentication context class reference; value of "acr" claim in JSON Web Token (JWT)
    private final String acrValue;

    LoginSecurityLevel(String acrValue) {
        this.acrValue = acrValue;
    }

    public static LoginSecurityLevel findByAcrValue(String acrValue) {
        return stream(values())
                .filter(level -> level.acrValue.equals(acrValue))
                .findFirst()
                .orElse(LoginSecurityLevel.NONE);
    }
}
