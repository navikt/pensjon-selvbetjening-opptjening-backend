package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InternalUserTokenGetterTest {

    private static final String ID_TOKEN = "id-token";
    private static MockWebServer server;
    private static String baseUrl;
    private WebClient webClient;
    @Mock
    OidcConfigGetter oidcConfigGetter;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = String.format("http://localhost:%s", server.getPort());
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void initialize() {
        webClient = WebClient.create();
    }

    @Test
    void getTokenData_returns_tokenData() {
        enqueueResponse(prepareTokenResponse());
        when(oidcConfigGetter.getTokenEndpoint()).thenReturn(baseUrl);
        var tokenGetter = new InternalUserTokenGetter(webClient, oidcConfigGetter, "client-id", "client-secret", "redirect-uri");

        TokenData tokenData = tokenGetter.getTokenData(TokenAccessParam.authorizationCode("code"));

        assertEquals(ID_TOKEN, tokenData.getIdToken());
    }

    private static String prepareTokenResponse() {
        return "{\n" +
                "  \"access_token\": \"access-token\",\n" +
                "  \"token_type\": \"Bearer\",\n" +
                "  \"expires_in\": 3599,\n" +
                "  \"scope\": \"https%3A%2F%2Fgraph.microsoft.com%2Fmail.read\",\n" +
                "  \"refresh_token\": \"refresh-token\",\n" +
                "  \"id_token\": \"" + ID_TOKEN + "\"\n" +
                "}";
    }

    private static void enqueueResponse(String responseBody) {
        var response = new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json");

        server.enqueue(response);
    }
}
