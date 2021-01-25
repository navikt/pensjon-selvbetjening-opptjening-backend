package no.nav.pensjon.selvbetjeningopptjening.consumer.skjerming;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkjermingConsumerTest {

    private static MockWebServer server;
    private static String baseUrl;
    private SkjermingConsumer consumer;

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
        consumer = new SkjermingConsumer(baseUrl);
    }

    @Test
    void isEgenAnsatt_returns_false_when_userIsNotSkjermet() {
        server.enqueue(response("false"));
        assertFalse(consumer.isEgenAnsatt(new Pid(TestFnrs.NORMAL)));
    }

    @Test
    void isEgenAnsatt_returns_true_when_userIsSkjermet() {
        server.enqueue(response("true"));
        assertTrue(consumer.isEgenAnsatt(new Pid(TestFnrs.NORMAL)));
    }

    private static MockResponse response(String isSkjermet) {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody(isSkjermet);
    }
}
