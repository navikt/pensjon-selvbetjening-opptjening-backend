package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
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

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@TestPropertySource(properties = "fnr=dummy")
class UttaksgradConsumerTest extends WebClientTest {

    private static final ServiceTokenData TOKEN = new ServiceTokenData("token", "type", LocalDateTime.MIN, 1L);
    private UttaksgradConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceTokenGetter tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenGetter.getServiceUserToken()).thenReturn(TOKEN);
        consumer = new UttaksgradConsumer(webClient, baseUrl(), tokenGetter);
    }

    @Test
    void should_return_listOfUttaksgrad_when_getAlderSakUttaksgradhistorikkForPerson() throws InterruptedException {
        prepare(uttaksgradForPersonResponse());

        List<Uttaksgrad> uttaksgrader = consumer.getAlderSakUttaksgradhistorikkForPerson("fnr");

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals("fnr", request.getHeader("pid"));
        assertEquals(0, uttaksgrader.size());
    }

    @Test
    void should_return_listOfUttaksgrad_when_getUttaksgradForVedtak() throws InterruptedException {
        prepare(uttaksgradForVedtakResponse());

        List<Uttaksgrad> uttaksgrader = consumer.getUttaksgradForVedtak(emptyList());

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals(0, uttaksgrader.size());
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
    }

    private static MockResponse uttaksgradForPersonResponse() {
        // Actual response from PEN
        return jsonResponse()
                .setBody("{\n" +
                        "    \"uttaksgradList\": []\n" +
                        "}");
    }

    private static MockResponse uttaksgradForVedtakResponse() {
        return jsonResponse()
                .setBody("{\n" +
                        "    \"uttaksgradList\": []\n" +
                        "}");
    }

    private static MockResponse pingResponse() {
        // Actual response from PEN
        return plaintextResponse()
                .setBody("Service online!");
    }
}
