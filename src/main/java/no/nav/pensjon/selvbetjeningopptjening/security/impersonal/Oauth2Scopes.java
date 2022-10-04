package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import static java.lang.String.join;

public class Oauth2Scopes {

    /**
     * OpenID - for obtaining ID token (application intends to use OIDC to verify user's identity)
     * https://openid.net/specs/openid-connect-core-1_0.html#Introduction
     */
    public static final String OPENID = "openid";

    /**
     * OIDC profile - for obtaining user's name
     * https://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims
     */
    public static final String PROFILE = "profile";

    /**
     * OIDC offline access - for obtaining refresh token
     * https://openid.net/specs/openid-connect-core-1_0.html#OfflineAccess
     */
    public static final String OFFLINE_ACCESS = "offline_access";

    /**
     * Azure AD default scope
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-permissions-and-consent#the-default-scope
     * Note: Cannot be combined with other scopes
     */
    public static final String AAD_DEFAULT = ".default";

    public static String combine(String... scopes) {
        return join("+", scopes);
    }

    private Oauth2Scopes() {
    }
}
