package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserToken;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.StsException;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@TestPropertySource(properties = "fnr=dummy")
class PensjonspoengConsumerTest extends WebClientTest {

    private static final ServiceUserToken TOKEN = new ServiceUserToken("token", 1L, "type");
    private PensjonspoengConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceUserTokenGetter tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenGetter.getServiceUserToken()).thenReturn(TOKEN);
        consumer = new PensjonspoengConsumer(webClient, baseUrl(), tokenGetter);
    }

    @Test
    void getPensjonspoengList_returns_pensjonspoengList_when_ok() throws InterruptedException {
        prepare(pensjonspoengResponse());

        List<Pensjonspoeng> pensjonspoengList = consumer.getPensjonspoengListe("fnr");

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        List<String> segments = requestUrl.pathSegments();
        assertEquals("pensjonspoeng", segments.get(0));
        assertEquals("fnr", segments.get(1));
        assertEquals(2, pensjonspoengList.size());
        Pensjonspoeng pensjonspoeng = pensjonspoengList.get(0);
        assertEquals(2018, pensjonspoeng.getYear());
        assertEquals(3.51D, pensjonspoeng.getPoeng());
        assertEquals("PPI", pensjonspoeng.getType());
        Inntekt inntekt = pensjonspoeng.getInntekt();
        assertEquals(431713L, inntekt.getBelop());
        assertEquals("SUM_PI", inntekt.getType());
        assertEquals(2018, inntekt.getYear());
        assertEquals(2009, pensjonspoengList.get(1).getYear());
        assertFalse(pensjonspoeng.hasOmsorg());
        assertNull(pensjonspoeng.getOmsorg());
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
        assertEquals("pensjonspoeng", segments.get(0));
        assertEquals("ping", segments.get(1));
    }

    private static MockResponse pensjonspoengResponse() {
        // Based on actual response from POPP
        return jsonResponse()
                .setBody("{\n" +
                        "    \"pensjonspoeng\": [\n" +
                        "        {\n" +
                        "            \"pensjonspoengId\": 543589853,\n" +
                        "            \"changeStamp\": {\n" +
                        "                \"createdBy\": \"srvpensjon\",\n" +
                        "                \"createdDate\": 1592309780836,\n" +
                        "                \"updatedBy\": \"srvpensjon\",\n" +
                        "                \"updatedDate\": 1592309780836\n" +
                        "            },\n" +
                        "            \"fnr\": \"23115225588\",\n" +
                        "            \"fnrOmsorgFor\": null,\n" +
                        "            \"kilde\": \"PEN\",\n" +
                        "            \"pensjonspoengType\": \"PPI\",\n" +
                        "            \"inntekt\": {\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"srvpensjon\",\n" +
                        "                    \"createdDate\": 1592309780718,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1592309780839\n" +
                        "                },\n" +
                        "                \"inntektId\": 585516176,\n" +
                        "                \"fnr\": \"23115225588\",\n" +
                        "                \"inntektAr\": 2018,\n" +
                        "                \"kilde\": \"POPP\",\n" +
                        "                \"kommune\": null,\n" +
                        "                \"piMerke\": null,\n" +
                        "                \"inntektType\": \"SUM_PI\",\n" +
                        "                \"belop\": 431713\n" +
                        "            },\n" +
                        "            \"omsorg\": null,\n" +
                        "            \"ar\": 2018,\n" +
                        "            \"anvendtPi\": 431713,\n" +
                        "            \"poeng\": 3.51,\n" +
                        "            \"maxUforegrad\": null\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"pensjonspoengId\": 543589757,\n" +
                        "            \"changeStamp\": {\n" +
                        "                \"createdBy\": \"srvpensjon\",\n" +
                        "                \"createdDate\": 1592309877037,\n" +
                        "                \"updatedBy\": \"srvpensjon\",\n" +
                        "                \"updatedDate\": 1592309877037\n" +
                        "            },\n" +
                        "            \"fnr\": \"23115225588\",\n" +
                        "            \"fnrOmsorgFor\": null,\n" +
                        "            \"kilde\": \"PEN\",\n" +
                        "            \"pensjonspoengType\": \"PPI\",\n" +
                        "            \"inntekt\": {\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"srvpensjon\",\n" +
                        "                    \"createdDate\": 1592309876937,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1592309877041\n" +
                        "                },\n" +
                        "                \"inntektId\": 585516080,\n" +
                        "                \"fnr\": \"23115225588\",\n" +
                        "                \"inntektAr\": 2009,\n" +
                        "                \"kilde\": \"POPP\",\n" +
                        "                \"kommune\": null,\n" +
                        "                \"piMerke\": null,\n" +
                        "                \"inntektType\": \"SUM_PI\",\n" +
                        "                \"belop\": 288434\n" +
                        "            },\n" +
                        "            \"omsorg\": null,\n" +
                        "            \"ar\": 2009,\n" +
                        "            \"anvendtPi\": 288434,\n" +
                        "            \"poeng\": 3.01,\n" +
                        "            \"maxUforegrad\": null\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}");
    }

    private static MockResponse pingResponse() {
        // POPP responds with 200 OK, no content
        return new MockResponse();
    }
}
