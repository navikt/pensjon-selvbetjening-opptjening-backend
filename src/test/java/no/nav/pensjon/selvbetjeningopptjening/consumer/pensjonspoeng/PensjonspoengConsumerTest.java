package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng;
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
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PensjonspoengConsumerTest extends WebClientTest {

    private static final ServiceTokenData TOKEN = new ServiceTokenData("token", "type", LocalDateTime.MIN, 1L);
    private PensjonspoengConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceTokenGetter tokenGetter;

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
        assertEquals("pensjonspoeng", segments.get(2));
        assertEquals("fnr", segments.get(3));
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
        assertEquals("pensjonspoeng", segments.get(2));
        assertEquals("ping", segments.get(3));
    }

    private static MockResponse pensjonspoengResponse() {
        // Based on actual response from POPP
        return jsonResponse()
                .setBody("""
                        {
                            "pensjonspoeng": [
                                {
                                    "pensjonspoengId": 543589853,
                                    "changeStamp": {
                                        "createdBy": "srvpensjon",
                                        "createdDate": 1592309780836,
                                        "updatedBy": "srvpensjon",
                                        "updatedDate": 1592309780836
                                    },
                                    "fnr": "23115225588",
                                    "fnrOmsorgFor": null,
                                    "kilde": "PEN",
                                    "pensjonspoengType": "PPI",
                                    "inntekt": {
                                        "changeStamp": {
                                            "createdBy": "srvpensjon",
                                            "createdDate": 1592309780718,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1592309780839
                                        },
                                        "inntektId": 585516176,
                                        "fnr": "23115225588",
                                        "inntektAr": 2018,
                                        "kilde": "POPP",
                                        "kommune": null,
                                        "piMerke": null,
                                        "inntektType": "SUM_PI",
                                        "belop": 431713
                                    },
                                    "omsorg": null,
                                    "ar": 2018,
                                    "anvendtPi": 431713,
                                    "poeng": 3.51,
                                    "maxUforegrad": null
                                },
                                {
                                    "pensjonspoengId": 543589757,
                                    "changeStamp": {
                                        "createdBy": "srvpensjon",
                                        "createdDate": 1592309877037,
                                        "updatedBy": "srvpensjon",
                                        "updatedDate": 1592309877037
                                    },
                                    "fnr": "23115225588",
                                    "fnrOmsorgFor": null,
                                    "kilde": "PEN",
                                    "pensjonspoengType": "PPI",
                                    "inntekt": {
                                        "changeStamp": {
                                            "createdBy": "srvpensjon",
                                            "createdDate": 1592309876937,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1592309877041
                                        },
                                        "inntektId": 585516080,
                                        "fnr": "23115225588",
                                        "inntektAr": 2009,
                                        "kilde": "POPP",
                                        "kommune": null,
                                        "piMerke": null,
                                        "inntektType": "SUM_PI",
                                        "belop": 288434
                                    },
                                    "omsorg": null,
                                    "ar": 2009,
                                    "anvendtPi": 288434,
                                    "poeng": 3.01,
                                    "maxUforegrad": null
                                }
                            ]
                        }""");
    }

    private static MockResponse pingResponse() {
        // POPP responds with 200 OK, no content
        return new MockResponse();
    }
}
