package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Oauth2ParamBuilderTest {

    @Test
    void buildTokenRequestMap_builds_map_with_nonEncoded_uri() {
        MultiValueMap<String, String> map = new Oauth2ParamBuilder()
                .clientId("client-id")
                .clientSecret("client-secret")
                .scope("scope1")
                .tokenAccessParam(TokenAccessParam.authorizationCode("auth-code"))
                .callbackUri("http://callback.uri")
                .buildClientIdTokenRequestMap();

        assertMapValue("client-id", map, "client_id");
        assertMapValue("client-secret", map, "client_secret");
        assertMapValue("scope1", map, "scope");
        assertMapValue("authorization_code", map, "grant_type");
        assertMapValue("auth-code", map, "code");
        assertMapValue("http://callback.uri", map, "redirect_uri");
    }

    @Test
    void buildAuthorizationUri_builds_uri_with_auth_params() {
        String uri = new Oauth2ParamBuilder()
                .scope("scope1+scope2+http%3A%2F%2Fscope.uri")
                .clientId("client-id")
                .callbackUri("http://callback.uri")
                .state("foo")
                .buildAuthorizationUri("http://auth.uri");

        assertEquals("http://auth.uri" +
                "?scope=scope1+scope2+http%3A%2F%2Fscope.uri" +
                "&response_type=code" +
                "&redirect_uri=http%3A%2F%2Fcallback.uri" +
                "&state=foo" +
                "&client_id=client-id" +
                "&response_mode=form_post", uri);
    }

    private static void assertMapValue(String expectedValue, MultiValueMap<String, String> map, String key) {
        assertEquals(expectedValue, map.get(key).get(0));
    }
}
