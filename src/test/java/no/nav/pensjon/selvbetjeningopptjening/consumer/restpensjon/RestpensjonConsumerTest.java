package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserToken;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.StsException;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
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

@SpringBootTest
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@TestPropertySource(properties = "fnr=dummy")
class RestpensjonConsumerTest extends WebClientTest {

    private static final ServiceUserToken TOKEN = new ServiceUserToken("token", 1L, "type");
    private RestpensjonConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceUserTokenGetter tokenGetter;

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
