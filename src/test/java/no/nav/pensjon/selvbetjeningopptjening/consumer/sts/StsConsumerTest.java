package no.nav.pensjon.selvbetjeningopptjening.consumer.sts;

import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.token.ServiceTokenData;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class StsConsumerTest {

    private static MockWebServer server;
    private static String baseUrl;
    private WebClient webClient;
    private StsConsumer tokenGetter;

    @Mock
    ExpirationChecker expirationChecker;

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
        webClient = spy(WebClient.create());
        tokenGetter = new StsConsumer(webClient, expirationChecker, baseUrl, "service-user", "secret");
    }

    @Test
    void getServiceUserToken_returns_serviceTokenUser_and_uses_cache() throws StsException {
        server.enqueue(tokenResponse());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);
        makeExpired(false);

        ServiceTokenData data = tokenGetter.getServiceUserToken();
        assertEquals("access-token", data.getAccessToken());
        assertEquals("Bearer", data.getTokenType());
        assertEquals(3600L, data.getExpiresInSeconds());
        verify(webClient, times(1)).get();

        ServiceTokenData cachedData = tokenGetter.getServiceUserToken();
        assertEquals("access-token", cachedData.getAccessToken());
        verify(webClient, times(1)).get(); // still 1 since cached value is used
    }

    @Test
    void getServiceUserToken_returns_serviceTokenUser_and_fetches_new_when_expired() throws StsException {
        server.enqueue(tokenResponse());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        makeExpired(false);
        ServiceTokenData response1 = tokenGetter.getServiceUserToken();
        assertEquals("access-token", response1.getAccessToken());
        assertEquals("Bearer", response1.getTokenType());
        assertEquals(3600L, response1.getExpiresInSeconds());
        verify(webClient, times(1)).get();

        makeExpired(true);
        server.enqueue(tokenResponse());
        ServiceTokenData response2 = tokenGetter.getServiceUserToken();
        assertEquals("access-token", response2.getAccessToken());
        verify(webClient, times(2)).get();
    }

    @Test
    void getServiceUserToken_throwsStsException_when_missingAuth() {
        server.enqueue(missingAuthResponse());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        var exception = assertThrows(StsException.class, () -> tokenGetter.getServiceUserToken());

        assertEquals(String.format("Failed to acquire service user token." +
                " Message: 401 Unauthorized from GET %s?grant_type=client_credentials&scope=openid." +
                " Response: %s.", baseUrl, unauthorizedMessage()),
                exception.getMessage());
        assertTrue(exception.getCause() instanceof WebClientResponseException);
    }

    private void makeExpired(boolean expired) {
        when(expirationChecker.isExpired(any(LocalDateTime.class), any(Long.class))).thenReturn(expired);
    }

    private static MockResponse tokenResponse() {
        return new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("{\n" +
                        "  \"access_token\": \"access-token\",\n" +
                        "  \"token_type\": \"Bearer\",\n" +
                        "  \"expires_in\": 3600\n" +
                        "}");
    }

    private static MockResponse missingAuthResponse() {
        return new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
                .setBody(unauthorizedMessage());
    }

    private static String unauthorizedMessage() {
        // Actual response from STS
        return "{\n" +
                "    \"error\": \"invalid_client\",\n" +
                "    \"error_description\": \"Unauthorised: Full authentication is required to access this resource\"\n" +
                "}";
    }
}
