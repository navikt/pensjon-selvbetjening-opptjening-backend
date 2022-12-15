package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamNames.*;

public class Oauth2ParamBuilder {

    private static final String OAUTH_2_RESPONSE_MODE = "form_post";
    private static final String OAUTH_2_RESPONSE_TYPE = "code";
    private static final String TOKEN_USE_ON_BEHALF_OF = "on_behalf_of";
    private static final String TOKEN_TYPE_JWT = "urn:ietf:params:oauth:token-type:jwt";

    // Ref. https://tools.ietf.org/html/rfc7523
    private static final String CLIENT_ASSERTION_TYPE_JWT_BEARER = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

    private String audience;
    private String clientAssertion;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String callbackUri;
    private String state;
    private TokenAccessParam accessParam;

    public Oauth2ParamBuilder audience(String value) {
        audience = value;
        return this;
    }

    public Oauth2ParamBuilder clientAssertion(String value) {
        clientAssertion = value;
        return this;
    }

    public Oauth2ParamBuilder clientId(String value) {
        clientId = value;
        return this;
    }

    public Oauth2ParamBuilder clientSecret(String value) {
        clientSecret = value;
        return this;
    }

    public Oauth2ParamBuilder scope(String value) {
        scope = value;
        return this;
    }

    public Oauth2ParamBuilder state(String value) {
        state = value;
        return this;
    }

    public Oauth2ParamBuilder callbackUri(String value) {
        callbackUri = value;
        return this;
    }

    public Oauth2ParamBuilder tokenAccessParam(TokenAccessParam value) {
        accessParam = value;
        return this;
    }

    public MultiValueMap<String, String> buildClientIdTokenRequestMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, accessParam.getGrantTypeName());
        map.add(accessParam.getParamName(), accessParam.getValue());
        map.add(SCOPE, scope);
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        map.add(REDIRECT_URI, callbackUri); // will be encoded later
        return map;
    }

    public MultiValueMap<String, String> buildClientCredentialsTokenRequestMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, accessParam.getGrantTypeName());
        map.add(accessParam.getParamName(), accessParam.getValue());
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        return map;
    }

    /**
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow
     */
    public MultiValueMap<String, String> buildOnBehalfOfTokenRequestMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, accessParam.getGrantTypeName());
        map.add(accessParam.getParamName(), accessParam.getValue());
        map.add(SCOPE, scope);
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        map.add(REQUESTED_TOKEN_USE, TOKEN_USE_ON_BEHALF_OF);
        return map;
    }

    public MultiValueMap<String, String> buildClientAssertionTokenRequestMapForTokenX() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, accessParam.getGrantTypeName());
        map.add(accessParam.getParamName(), accessParam.getValue());
        map.add(CLIENT_ASSERTION_TYPE, CLIENT_ASSERTION_TYPE_JWT_BEARER);
        map.add(CLIENT_ASSERTION, clientAssertion);
        map.add(AUDIENCE, audience);
        map.add(SUBJECT_TOKEN_TYPE, TOKEN_TYPE_JWT);
        return map;
    }

    public String buildAuthorizationUri(String endpoint) {
        return endpoint + "?" +
                buildAuthorizationMap()
                        .entrySet()
                        .stream()
                        .map(kv -> kv.getKey() + "=" + kv.getValue())
                        .collect(Collectors.joining("&"));
    }

    private Map<String, String> buildAuthorizationMap() {
        Map<String, String> map = new HashMap<>();
        map.put(SCOPE, scope); // do not encode scope (need to preserve '+' signs)
        map.put(RESPONSE_MODE, OAUTH_2_RESPONSE_MODE);
        map.put(RESPONSE_TYPE, OAUTH_2_RESPONSE_TYPE);
        map.put(REDIRECT_URI, encode(callbackUri));
        map.put(CLIENT_ID, clientId);
        map.put(STATE, encode(state));
        return map;
    }

    private static String encode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}
