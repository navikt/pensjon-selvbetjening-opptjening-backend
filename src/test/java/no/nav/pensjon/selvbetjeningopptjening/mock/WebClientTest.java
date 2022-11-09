package no.nav.pensjon.selvbetjeningopptjening.mock;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.*;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;

@SpringBootTest
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@TestPropertySource(properties = "fnr=dummy")
@ActiveProfiles("test")
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

    protected static RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    protected static MockResponse jsonResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    protected static MockResponse plaintextResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
    }

    protected static MockResponse jsonResponse(HttpStatus status) {
        return jsonResponse()
                .setResponseCode(status.value());
    }

    protected static String baseUrl() {
        return baseUrl;
    }
}
