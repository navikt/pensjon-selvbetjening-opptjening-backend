package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

/**
 * www.iana.org/assignments/oauth-parameters/oauth-parameters.xhtml
 */
public final class Oauth2ParamNames {

    static final String ASSERTION = "assertion";
    static final String AUDIENCE = "audience";
    static final String CLIENT_ASSERTION = "client_assertion";
    static final String CLIENT_ASSERTION_TYPE = "client_assertion_type";
    static final String CLIENT_ID = "client_id";
    static final String CLIENT_SECRET = "client_secret";
    static final String CODE = "code";
    static final String GRANT_TYPE = "grant_type";
    static final String REDIRECT_URI = "redirect_uri";
    static final String REFRESH_TOKEN = "refresh_token";
    static final String REQUESTED_TOKEN_USE = "requested_token_use";
    static final String RESPONSE_MODE = "response_mode";
    static final String RESPONSE_TYPE = "response_type";
    static final String SCOPE = "scope";
    static final String STATE = "state";
    static final String SUBJECT_TOKEN = "subject_token";
    static final String SUBJECT_TOKEN_TYPE = "subject_token_type";

    private Oauth2ParamNames() {
    }
}
