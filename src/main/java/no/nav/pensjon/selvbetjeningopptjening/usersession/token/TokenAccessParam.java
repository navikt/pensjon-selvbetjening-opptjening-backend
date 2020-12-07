package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.GrantType;

import static java.util.Objects.requireNonNull;

public class TokenAccessParam {

    private final GrantType type;
    private final String value;

    private TokenAccessParam(GrantType type, String value) {
        this.type = requireNonNull(type);
        this.value = requireNonNull(value);
    }

    public String getGrantTypeName() {
        return type.name;
    }

    public String getParamName() {
        return type.paramName;
    }

    public String getValue() {
        return value;
    }

    public static TokenAccessParam authorizationCode(String code) {
        return new TokenAccessParam(GrantType.AUTHORIZATION_CODE, code);
    }

    static TokenAccessParam refreshToken(String token) {
        return new TokenAccessParam(GrantType.REFRESH_TOKEN, token);
    }
}
