package no.nav.pensjon.selvbetjeningopptjening.security.token.client;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ClientIdBasedTokenClientTest {

    private static final String ID_TOKEN = "id-token";
    private static MockWebServer server;
    private static String baseUrl;
    private WebClient webClient;

    @Mock
    private Oauth2ConfigGetter oauth2ConfigGetter;
    @Mock
    private ExpirationChecker expirationChecker;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = format("http://localhost:%s", server.getPort());
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
        when(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl);
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);
        var tokenGetter = new ClientIdBasedTokenClient(webClient, oauth2ConfigGetter, expirationChecker, "n/a", "pensjon-selvbetjening-soknad-alder-backend", "client-id", "client-secret", "redirect-uri");

        TokenData tokenData = tokenGetter.getTokenData(TokenAccessParam.authorizationCode("code"), "audience");

        assertEquals(ID_TOKEN, tokenData.getIdToken());
    }

    private static String prepareTokenResponse() {
        return format("""
                {
                  "access_token": "access-token",
                  "token_type": "Bearer",
                  "expires_in": 3599,
                  "scope": "https%%3A%%2F%%2Fgraph.microsoft.com%%2Fmail.read",
                  "refresh_token": "refresh-token",
                  "id_token": "%s"
                }""", ID_TOKEN);
    }

    private static void enqueueResponse(String responseBody) {
        var response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody(responseBody);

        server.enqueue(response);
    }
}
