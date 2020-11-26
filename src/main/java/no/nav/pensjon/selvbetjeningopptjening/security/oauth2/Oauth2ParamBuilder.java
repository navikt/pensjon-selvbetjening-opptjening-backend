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

    // Ref. https://tools.ietf.org/html/rfc7523
    private static final String CLIENT_ASSERTION_TYPE_JWT_BEARER = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

    private String clientAssertion;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String callbackUri;
    private String state;
    private TokenAccessParam accessParam;

    public Oauth2ParamBuilder clientAssertion(String value) {
        this.clientAssertion = value;
        return this;
    }

    public Oauth2ParamBuilder clientId(String value) {
        this.clientId = value;
        return this;
    }

    public Oauth2ParamBuilder clientSecret(String value) {
        this.clientSecret = value;
        return this;
    }

    public Oauth2ParamBuilder scope(String value) {
        this.scope = value;
        return this;
    }

    public Oauth2ParamBuilder state(String value) {
        this.state = value;
        return this;
    }

    public Oauth2ParamBuilder callbackUri(String value) {
        this.callbackUri = value;
        return this;
    }

    public Oauth2ParamBuilder tokenAccessParam(TokenAccessParam value) {
        this.accessParam = value;
        return this;
    }

    public MultiValueMap<String, String> buildClientIdTokenRequestMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(SCOPE, scope);
        map.add(GRANT_TYPE, accessParam.getGrantTypeName());
        map.add(accessParam.getParamName(), accessParam.getValue());
        map.add(REDIRECT_URI, callbackUri); // will be encoded later
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        return map;
    }

    public MultiValueMap<String, String> buildClientAssertionTokenRequestMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(GRANT_TYPE, accessParam.getGrantTypeName());
        map.add(accessParam.getParamName(), accessParam.getValue());
        map.add(CLIENT_ASSERTION_TYPE, CLIENT_ASSERTION_TYPE_JWT_BEARER);
        map.add(CLIENT_ASSERTION, clientAssertion);
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
        map.put(STATE, state);
        return map;
    }

    private static String encode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}
