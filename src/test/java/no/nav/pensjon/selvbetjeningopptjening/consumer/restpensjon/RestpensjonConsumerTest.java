package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestpensjonConsumerTest extends WebClientTest {

    private RestpensjonConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @BeforeEach
    void initialize() {
        consumer = new RestpensjonConsumer(webClient, baseUrl());
    }

    @Test
    void getRestpensjonListe_returns_restpensjoner_when_ok() throws InterruptedException {
        prepare(restpensjonResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSOPPTJENING_REGISTER.appName)) {
            List<Restpensjon> restpensjoner = consumer.getRestpensjonListe("fnr");

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            List<String> segments = requestUrl.pathSegments();
            assertEquals("restpensjon", segments.get(2));
            assertEquals("fnr", segments.get(3));
            assertEquals("false", requestUrl.queryParameter("hentSiste"));
            assertEquals(0, restpensjoner.size());
        }
    }

    @Test
    void ping_ok() throws InterruptedException {
        prepare(pingResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSOPPTJENING_REGISTER.appName)) {
            consumer.ping();

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            List<String> segments = requestUrl.pathSegments();
            assertEquals("restpensjon", segments.get(2));
            assertEquals("ping", segments.get(3));
        }
    }

    private static MockResponse restpensjonResponse() {
        // Actual response from POPP
        return jsonResponse()
                .setBody("""
                        {
                            "restpensjoner": []
                        }""");
    }

    private static MockResponse pingResponse() {
        // POPP responds with 200 OK, no content
        return new MockResponse();
    }
}
