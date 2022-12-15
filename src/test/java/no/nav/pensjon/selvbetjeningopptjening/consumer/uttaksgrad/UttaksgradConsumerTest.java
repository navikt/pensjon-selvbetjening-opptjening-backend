package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UttaksgradConsumerTest extends WebClientTest {

    private UttaksgradConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @BeforeEach
    void initialize() {
        consumer = new UttaksgradConsumer(webClient, baseUrl());
    }

    @Test
    void should_return_listOfUttaksgrad_when_getAlderSakUttaksgradhistorikkForPerson() throws InterruptedException {
        prepare(uttaksgradForPersonResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSFAGLIG_KJERNE.appName)) {
            List<Uttaksgrad> uttaksgrader = consumer.getAlderSakUttaksgradhistorikkForPerson("fnr");

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            assertEquals("fnr", request.getHeader("pid"));
            assertEquals(0, uttaksgrader.size());
        }
    }

    @Test
    void should_return_listOfUttaksgrad_when_getUttaksgradForVedtak() throws InterruptedException {
        prepare(uttaksgradForVedtakResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSFAGLIG_KJERNE.appName)) {
            List<Uttaksgrad> uttaksgrader = consumer.getUttaksgradForVedtak(emptyList());

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            assertEquals(0, uttaksgrader.size());
        }
    }

    @Test
    void ping_ok() throws InterruptedException {
        prepare(pingResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSFAGLIG_KJERNE.appName)) {
            consumer.ping();

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
        }
    }

    private static MockResponse uttaksgradForPersonResponse() {
        // Actual response from PEN
        return jsonResponse()
                .setBody("""
                        {
                            "uttaksgradList": []
                        }""");
    }

    private static MockResponse uttaksgradForVedtakResponse() {
        return jsonResponse()
                .setBody("""
                        {
                            "uttaksgradList": []
                        }""");
    }

    private static MockResponse pingResponse() {
        // Actual response from PEN
        return plaintextResponse()
                .setBody("Service online!");
    }
}
