package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.security.token.ServiceTokenData;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class RestpensjonConsumerTest extends WebClientTest {

    private static final ServiceTokenData TOKEN = new ServiceTokenData("token", "type", LocalDateTime.MIN, 1L);
    private RestpensjonConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceTokenGetter tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenGetter.getServiceUserToken()).thenReturn(TOKEN);
        consumer = new RestpensjonConsumer(webClient, baseUrl(), tokenGetter);
    }

    @Test
    void getRestpensjonListe_returns_restpensjoner_when_ok() throws InterruptedException {
        prepare(restpensjonResponse());

        List<Restpensjon> restpensjoner = consumer.getRestpensjonListe("fnr");

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        List<String> segments = requestUrl.pathSegments();
        assertEquals("restpensjon", segments.get(0));
        assertEquals("fnr", segments.get(1));
        assertEquals("false", requestUrl.queryParameter("hentSiste"));
        assertEquals(0, restpensjoner.size());
    }

    @Test
    void ping_ok() throws InterruptedException {
        prepare(pingResponse());

        consumer.ping();

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        List<String> segments = requestUrl.pathSegments();
        assertEquals("restpensjon", segments.get(0));
        assertEquals("ping", segments.get(1));
    }

    private static MockResponse restpensjonResponse() {
        // Actual response from POPP
        return jsonResponse()
                .setBody("{\n" +
                        "    \"restpensjoner\": []\n" +
                        "}");
    }

    private static MockResponse pingResponse() {
        // POPP responds with 200 OK, no content
        return new MockResponse();
    }
}
