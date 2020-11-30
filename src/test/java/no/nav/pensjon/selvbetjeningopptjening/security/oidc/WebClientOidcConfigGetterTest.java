package no.nav.pensjon.selvbetjeningopptjening.security.oidc;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

@ExtendWith(SpringExtension.class)
class WebClientOidcConfigGetterTest {

    private static MockWebServer server;
    private static String baseUrl;
    private static String oidcConfigResponse;
    private OidcConfigGetter configGetter;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = String.format("http://localhost:%s", server.getPort());
        oidcConfigResponse = prepareOidcConfigResponse(baseUrl);
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void initialize() {
        WebClient webClient = spy(WebClient.create());
        configGetter = new WebClientOidcConfigGetter(webClient, baseUrl);
    }

    @Test
    void getIssuer_returns_TokenEndpoint_and_usesCache() {
        testValueReturnAndCacheUse(configGetter::getIssuer);
    }

    @Test
    void getAuthorizationEndpoint_returns_AuthorizationEndpoint_and_usesCache() {
        testValueReturnAndCacheUse(configGetter::getAuthorizationEndpoint);
    }

    @Test
    void getTokenEndpoint_returns_TokenEndpoint_and_usesCache() {
        testValueReturnAndCacheUse(configGetter::getTokenEndpoint);
    }

    @Test
    void getJsonWebKeySetUri_returns_JsonWebKeySetUri_and_usesCache() {
        testValueReturnAndCacheUse(configGetter::getJsonWebKeySetUri);
    }

    private void testValueReturnAndCacheUse(Supplier<String> valueReturner) {
        enqueueResponse(oidcConfigResponse);
        String initialValue = valueReturner.get();
        modifyResponse();
        String cachedValue = valueReturner.get();
        assertEquals(cachedValue, initialValue);

        // Refresh will cause new web call in the get() that follows:
        configGetter.refresh();
        String freshValue = valueReturner.get();
        assertEquals(initialValue + "/new", freshValue);
    }

    private static void modifyResponse() {
        enqueueResponse(prepareOidcConfigResponse(baseUrl, "/new"));
    }

    private static void enqueueResponse(String responseBody) {
        var response = new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json");

        server.enqueue(response);
    }

    static String prepareOidcConfigResponse(String url) {
        return prepareOidcConfigResponse(url, "");
    }

    private static String prepareOidcConfigResponse(String url, String modification) {
        return "{\n" +
                "  \"token_endpoint\": \"" + url + modification + "\",\n" +
                "  \"token_endpoint_auth_methods_supported\": [\"client_secret_post\", \"private_key_jwt\", \"client_secret_basic\"],\n" +
                "  \"jwks_uri\": \"" + url + modification + "\",\n" +
                "  \"response_modes_supported\": [\"query\", \"fragment\", \"form_post\"],\n" +
                "  \"subject_types_supported\": [\"pairwise\"],\n" +
                "  \"id_token_signing_alg_values_supported\": [\"RS256\"],\n" +
                "  \"response_types_supported\": [\"code\", \"id_token\", \"code id_token\", \"id_token token\"],\n" +
                "  \"scopes_supported\": [\"openid\", \"profile\", \"email\", \"offline_access\"],\n" +
                "  \"issuer\": \"www.issuer.org" + modification + "\",\n" +
                "  \"request_uri_parameter_supported\": false,\n" +
                "  \"userinfo_endpoint\": \"" + url + "\",\n" +
                "  \"authorization_endpoint\": \"" + url + modification + "\",\n" +
                "  \"device_authorization_endpoint\": \"" + url + "\",\n" +
                "  \"http_logout_supported\": true,\n" +
                "  \"frontchannel_logout_supported\": true,\n" +
                "  \"end_session_endpoint\": \"" + url + "\",\n" +
                "  \"claims_supported\": [\"sub\", \"iss\", \"cloud_instance_name\", \"cloud_instance_host_name\", \"cloud_graph_host_name\", \"msgraph_host\", \"aud\", \"exp\", \"iat\", \"auth_time\", \"acr\", \"nonce\", \"preferred_username\", \"name\", \"tid\", \"ver\", \"at_hash\", \"c_hash\", \"email\"],\n" +
                "  \"tenant_region_scope\": \"EU\",\n" +
                "  \"cloud_instance_name\": \"microsoftonline.com\",\n" +
                "  \"cloud_graph_host_name\": \"graph.windows.net\",\n" +
                "  \"msgraph_host\": \"graph.microsoft.com\",\n" +
                "  \"rbac_url\": \"" + url + "\"\n" +
                "}";
    }
}
