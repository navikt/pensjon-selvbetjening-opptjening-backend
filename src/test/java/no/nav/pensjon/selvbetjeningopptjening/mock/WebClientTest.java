package no.nav.pensjon.selvbetjeningopptjening.mock;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public class WebClientTest {

    private static MockWebServer server;
    private static String baseUrl;

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

    protected static void prepare(MockResponse response) {
        server.enqueue(response);
    }

    protected static MockResponse jsonResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    protected static MockResponse jsonResponse(HttpStatus status) {
        return jsonResponse()
                .setResponseCode(status.value());
    }

    protected static String baseUrl() {
        return baseUrl;
    }
}
