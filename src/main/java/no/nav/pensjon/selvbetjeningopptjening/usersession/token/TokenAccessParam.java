package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.GrantType;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RefreshToken;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class TokenAccessParam {

    private final GrantType type;
    private final String value;

    private TokenAccessParam(GrantType type, String value) {
        this.type = requireNonNull(type, "type");
        this.value = requireNonNull(value, "value");
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

    public static TokenAccessParam clientCredentials(String scope) {
        return new TokenAccessParam(GrantType.CLIENT_CREDENTIALS, scope);
    }

    public static TokenAccessParam jwtBearer(String assertion) {
        return new TokenAccessParam(GrantType.JWT_BEARER, assertion);
    }

    public static TokenAccessParam refreshToken(RefreshToken token) {
        return new TokenAccessParam(GrantType.REFRESH_TOKEN, token.getValue());
    }

    public static TokenAccessParam tokenExchange(String subjectToken) {
        return new TokenAccessParam(GrantType.TOKEN_EXCHANGE, subjectToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (TokenAccessParam) o;
        return type == that.type && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
