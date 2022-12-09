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
        prepare(fullmektigResponse());

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

    /**
     * Eksempelrespons fra pensjon-fullmakt.
     * Aktøren (personen) med fødselsnummer 05845997316 er en fullmektig.
     */
    private static MockResponse fullmektigResponse() {
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
}
