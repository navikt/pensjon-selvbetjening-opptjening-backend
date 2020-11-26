package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

public enum GrantType {

    AUTHORIZATION_CODE("authorization_code", Oauth2ParamNames.CODE),
    REFRESH_TOKEN("refresh_token", Oauth2ParamNames.REFRESH_TOKEN);

    public final String name;
    public final String paramName;

    GrantType(String name, String paramName) {
        this.name = name;
        this.paramName = paramName;
    }
}
