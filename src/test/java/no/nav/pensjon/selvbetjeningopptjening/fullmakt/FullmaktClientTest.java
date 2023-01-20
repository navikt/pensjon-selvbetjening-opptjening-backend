package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FullmaktClientTest extends WebClientTest {

    private FullmaktClient consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @BeforeEach
    void initialize() {
        consumer = new FullmaktClient(webClient, baseUrl());
    }

    @Test
    void getFullmakter_returns_fullmakt_for_fullmektig_when_such_fullmakt_exists() throws InterruptedException {
        prepare(personligFullmaktResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            List<Fullmakt> fullmakter = consumer.getFullmakter(new Pid("05845997316"), true);

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            assertEquals("05845997316", request.getHeader("aktorNr"));
            assertEquals(1, fullmakter.size());
            Fullmakt fullmakt = fullmakter.get(0);
            assertEquals(Fagomraade.PEN, fullmakt.getFagomrade());
            assertEquals(Fullmakttype.SELVBET, fullmakt.getType());
            assertEquals(Fullmaktnivaa.FULLSTENDIG, fullmakt.getNivaa());
            assertEquals(9, fullmakt.getVersjon());
            assertEquals(LocalDate.of(2022, 6, 15), fullmakt.getFom());
            assertEquals(LocalDate.of(999999999, 12, 31), fullmakt.getTom());
            assertTrue(fullmakt.lastsForever());
            assertTrue(fullmakt.isGyldig());
            assertEquals("01865499538", fullmakt.getGiver().aktoernummer());
            assertEquals("05845997316", fullmakt.getFullmektig().aktoernummer());
        }
    }

    @Test
    void getFullmakter_returns_fullmakter_for_samhandler_when_such_fullmakter_exists() throws InterruptedException {
        prepare(samhandlerFullmaktResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            List<Fullmakt> fullmakter = consumer.getFullmakter(new Pid("05845997316"), true);

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            assertEquals("05845997316", request.getHeader("aktorNr"));
            assertEquals(2, fullmakter.size());
            Fullmakt fullmakt1 = fullmakter.get(0);
            assertEquals(Fagomraade.PEN, fullmakt1.getFagomrade());
            assertEquals(Fullmakttype.SELVBET, fullmakt1.getType());
            assertEquals(Fullmaktnivaa.SAMORDPLIK, fullmakt1.getNivaa());
            assertEquals(136, fullmakt1.getVersjon());
            assertEquals(LocalDate.of(2022, 12, 13), fullmakt1.getFom());
            assertEquals(LocalDate.of(2023, 6, 13), fullmakt1.getTom());
            assertFalse(fullmakt1.lastsForever());
            assertTrue(fullmakt1.isGyldig());
            assertEquals("80000470767", fullmakt1.getGiver().aktoernummer());
            assertEquals("SAMHANDLER", fullmakt1.getGiver().type());
            assertEquals("05845997316", fullmakt1.getFullmektig().aktoernummer());
            assertEquals("PERSON", fullmakt1.getFullmektig().type());
            Fullmakt fullmakt2 = fullmakter.get(1);
            assertEquals(Fagomraade.PEN, fullmakt2.getFagomrade());
            assertEquals(Fullmakttype.SELVBET, fullmakt2.getType());
            assertEquals(Fullmaktnivaa.SAMORDPLIK, fullmakt2.getNivaa());
            assertEquals(11, fullmakt2.getVersjon());
            assertEquals(LocalDate.of(2022, 9, 2), fullmakt2.getFom());
            assertEquals(LocalDate.of(2023, 3, 2), fullmakt2.getTom());
            assertFalse(fullmakt2.lastsForever());
            assertTrue(fullmakt2.isGyldig());
            assertEquals("80000750000", fullmakt2.getGiver().aktoernummer());
            assertEquals("SAMHANDLER", fullmakt2.getGiver().type());
            assertEquals("05845997316", fullmakt2.getFullmektig().aktoernummer());
            assertEquals("PERSON", fullmakt2.getFullmektig().type());
        }
    }

    /**
     * Eksempelrespons fra pensjon-fullmakt for personlig fullmakt.
     * Aktøren (personen) med fødselsnummer 05845997316 er en fullmektig.
     */
    private static MockResponse personligFullmaktResponse() {
        return jsonResponse()
                .setBody("""
                        {
                            "aktor": {
                                "kodeAktorType": "PERSON",
                                "aktorNr": "05845997316",
                                "fullmaktTil": [
                                    {
                                        "sistBrukt": 1655236800000,
                                        "kodeFullmaktType": "SELVBET",
                                        "aktorMottar": {
                                            "kodeAktorType": "PERSON",
                                            "aktorNr": "05845997316",
                                            "fullmaktTil": [],
                                            "fullmaktFra": []
                                        },
                                        "aktorGir": {
                                            "kodeAktorType": "PERSON",
                                            "aktorNr": "01865499538",
                                            "fullmaktTil": [],
                                            "fullmaktFra": []
                                        },
                                        "opprettetDato": 1655245613272,
                                        "kodeFullmaktNiva": "FULLSTENDIG",
                                        "opprettetAv": "unknown-user",
                                        "endretDato": 1655245613272,
                                        "endretAv": "unknown-user",
                                        "fomDato": 1655244000000,
                                        "tomDato": null,
                                        "gyldig": true,
                                        "fullmaktId": 100214911,
                                        "versjon": 9,
                                        "fagomrade": "PEN"
                                    }
                                ],
                                "fullmaktFra": []
                            }
                        }""");
    }

    /**
     * Eksempelrespons fra pensjon-fullmakt for samhandlerfullmakt.
     * Aktøren (personen) med fødselsnummer 05845997316 er en fullmektig (ansatt hos samhandlerorganisasjonen).
     */
    private static MockResponse samhandlerFullmaktResponse() {
        return jsonResponse()
                .setBody("""
{
    "aktor": {
        "kodeAktorType": "PERSON",
        "aktorNr": "05845997316",
        "fullmaktTil": [
            {
                "sistBrukt": 1668549600000,
                "kodeFullmaktType": "SELVBET",
                "aktorMottar": {
                    "kodeAktorType": "PERSON",
                    "aktorNr": "05845997316",
                    "fullmaktTil": [],
                    "fullmaktFra": []
                },
                "aktorGir": {
                    "kodeAktorType": "SAMHANDLER",
                    "aktorNr": "80000470767",
                    "fullmaktTil": [],
                    "fullmaktFra": []
                },
                "opprettetDato": 1521631957485,
                "kodeFullmaktNiva": "SAMORDPLIK",
                "opprettetAv": "01865499538",
                "endretDato": 1670939549423,
                "endretAv": "01865499538",
                "fomDato": 1670886000000,
                "tomDato": 1686607200000,
                "gyldig": true,
                "fullmaktId": 100210118,
                "versjon": 136,
                "fagomrade": "PEN"
            },
            {
                "sistBrukt": 1662321600000,
                "kodeFullmaktType": "SELVBET",
                "aktorMottar": {
                    "kodeAktorType": "PERSON",
                    "aktorNr": "05845997316",
                    "fullmaktTil": [],
                    "fullmaktFra": []
                },
                "aktorGir": {
                    "kodeAktorType": "SAMHANDLER",
                    "aktorNr": "80000750000",
                    "fullmaktTil": [],
                    "fullmaktFra": []
                },
                "opprettetDato": 1521632345233,
                "kodeFullmaktNiva": "SAMORDPLIK",
                "opprettetAv": "01865499538",
                "endretDato": 1662108940189,
                "endretAv": "01865499538",
                "fomDato": 1662069600000,
                "tomDato": 1677711600000,
                "gyldig": true,
                "fullmaktId": 100210120,
                "versjon": 11,
                "fagomrade": "PEN"
            }
        ],
        "fullmaktFra": []
    }
}""");
    }
}
