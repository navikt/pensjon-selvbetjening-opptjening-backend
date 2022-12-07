package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

/**
 * RFCs related to grant type:
 * - authorization_code, client_credentials, refresh_token: https://tools.ietf.org/html/rfc6749
 * - jwt-bearer: https://tools.ietf.org/html/rfc7523
 * - assertion: https://tools.ietf.org/html/rfc7521
 * - token-exchange: https://tools.ietf.org/html/rfc8693
 */
public enum GrantType {

    AUTHORIZATION_CODE("authorization_code", Oauth2ParamNames.CODE),
    CLIENT_CREDENTIALS("client_credentials", Oauth2ParamNames.SCOPE),
    REFRESH_TOKEN("refresh_token", Oauth2ParamNames.REFRESH_TOKEN),
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer", Oauth2ParamNames.ASSERTION),
    TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange", Oauth2ParamNames.SUBJECT_TOKEN);

    public final String name;
    public final String paramName;

    GrantType(String name, String paramName) {
        this.name = name;
        this.paramName = paramName;
    }
}
