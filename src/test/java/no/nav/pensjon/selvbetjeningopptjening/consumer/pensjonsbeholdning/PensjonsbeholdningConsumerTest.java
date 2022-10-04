package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PensjonsbeholdningConsumerTest extends WebClientTest {

    private static final String CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe";
    private PensjonsbeholdningConsumer consumer;

    private static final String EXPECTED_GENERAL_ERROR_MESSAGE = "Error when calling the external service " +
            CONSUMED_SERVICE + " in " + POPP + ".";

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    private TokenGetterFacade tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenGetter.getToken(anyString())).thenReturn("token");
        consumer = new PensjonsbeholdningConsumer(webClient, baseUrl(), tokenGetter);
    }

    @Test
    @Order(1) // Fails if run after one of the exception tests
    void test_ping() throws InterruptedException {
        prepare(new MockResponse());

        consumer.ping();

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @Order(2)
    void should_return_listOfBeholdning_when_getPensjonsbeholdning() throws InterruptedException {
        prepare(okResponse());

        List<Beholdning> beholdninger = consumer.getPensjonsbeholdning("fnr");
        RecordedRequest request = takeRequest();

        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals(2, beholdninger.size());
        Beholdning beholdning = beholdninger.get(0);
        assertEquals(430456711L, beholdning.getId());
        assertEquals("12117121168", beholdning.getFnr());
        assertEquals("G", beholdning.getStatus());
        assertEquals("PEN_B", beholdning.getType());
        assertEquals(20137.460428429236D, beholdning.getBelop());
        assertFalse(beholdning.hasVedtak());
        assertEquals(LocalDate.of(1994, 1, 1), beholdning.getFomDato());
        assertEquals(LocalDate.of(1994, 12, 31), beholdning.getTomDato());
        assertEquals(108655.1D, beholdning.getGrunnlag());
        assertEquals(108655.0D, beholdning.getGrunnlagAvkortet());
        assertEquals(20137.460428429236D, beholdning.getInnskudd());
        assertEquals(19666.555000000004D, beholdning.getInnskuddUtenOmsorg());
        assertEquals("NY_OPPTJENING", beholdning.getOppdateringArsak());
        assertNull(beholdning.getLonnsvekstregulering());
    }

    @Test
    @Order(3)
    void should_return_FailedCallingExternalServiceException_when_unauthorized() {
        prepare(unauthorizedResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning("fnr"));

        assertThat(thrown.getMessage(), is(EXPECTED_GENERAL_ERROR_MESSAGE + " Received 401 UNAUTHORIZED"));
    }

    @Test
    @Order(4)
    void should_return_FailedCallingExternalServiceException_when_nonExistentPerson() {
        prepare(nonExistentPersonResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning("fnr"));

        assertThat(thrown.getMessage(), is(EXPECTED_GENERAL_ERROR_MESSAGE + " Person ikke funnet"));
    }

    @Test
    @Order(5)
    void should_return_FailedCallingExternalServiceException_when_invalidPid() {
        prepare(invalidPidResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(),
                is(EXPECTED_GENERAL_ERROR_MESSAGE + " An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    private static MockResponse okResponse() {
        return jsonResponse()
                .setBody("""
                        {
                            "beholdninger": [
                                {
                                    "beholdningId": 430456711,
                                    "fnr": "12117121168",
                                    "status": "G",
                                    "beholdningType": "PEN_B",
                                    "belop": 20137.460428429236,
                                    "vedtakId": null,
                                    "fomDato": 757378800000,
                                    "tomDato": 788828400000,
                                    "beholdningGrunnlag": 108655.1,
                                    "beholdningGrunnlagAvkortet": 108655.0,
                                    "beholdningInnskudd": 20137.460428429236,
                                    "beholdningInnskuddUtenOmsorg": 19666.555000000004,
                                    "oppdateringArsak": "NY_OPPTJENING",
                                    "lonnsvekstregulering": null,
                                    "inntektOpptjeningBelop": {
                                        "inntektOpptjeningBelopId": 302319479,
                                        "ar": 1992,
                                        "belop": 108655.0,
                                        "sumPensjonsgivendeInntekt": {
                                            "changeStamp": {
                                                "createdBy": "srvpensjon",
                                                "createdDate": 1586931809567,
                                                "updatedBy": "srvpensjon",
                                                "updatedDate": 1586931813383
                                            },
                                            "inntektId": 585473360,
                                            "fnr": "12117121168",
                                            "inntektAr": 1992,
                                            "kilde": "POPP",
                                            "kommune": null,
                                            "piMerke": null,
                                            "inntektType": "SUM_PI",
                                            "belop": 108655
                                        },
                                        "inntektListe": [
                                            {
                                                "changeStamp": {
                                                    "createdBy": "TESTDATA",
                                                    "createdDate": 1586931809059,
                                                    "updatedBy": "srvpensjon",
                                                    "updatedDate": 1586931809534
                                                },
                                                "inntektId": 585473359,
                                                "fnr": "12117121168",
                                                "inntektAr": 1992,
                                                "kilde": "PEN",
                                                "kommune": "1337",
                                                "piMerke": null,
                                                "inntektType": "INN_LON",
                                                "belop": 108655
                                            }
                                        ],
                                        "changeStamp": {
                                            "createdBy": "srvpensjon",
                                            "createdDate": 1586931854757,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1586931854757
                                        }
                                    },
                                    "omsorgOpptjeningBelop": null,
                                    "dagpengerOpptjeningBelop": null,
                                    "forstegangstjenesteOpptjeningBelop": null,
                                    "uforeOpptjeningBelop": null,
                                    "changeStamp": {
                                        "createdBy": "srvpensjon",
                                        "createdDate": 1586931854758,
                                        "updatedBy": "srvpensjon",
                                        "updatedDate": 1586931854758
                                    }
                                },
                                {
                                    "beholdningId": 430457040,
                                    "fnr": "12117121168",
                                    "status": "G",
                                    "beholdningType": "PEN_B",
                                    "belop": 1317010.8630999844,
                                    "vedtakId": null,
                                    "fomDato": 1525125600000,
                                    "tomDato": null,
                                    "beholdningGrunnlag": 275611.0,
                                    "beholdningGrunnlagAvkortet": 275611.0,
                                    "beholdningInnskudd": 50913.322334762146,
                                    "beholdningInnskuddUtenOmsorg": 49885.59100000001,
                                    "oppdateringArsak": "REGULERING",
                                    "lonnsvekstregulering": {
                                        "lonnsvekstreguleringId": 318421599,
                                        "reguleringsbelop": 44167.659176156856,
                                        "reguleringsDato": 1525125600000,
                                        "changeStamp": {
                                            "createdBy": "srvpensjon",
                                            "createdDate": 1586931866338,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1586931866338
                                        }
                                    },
                                    "inntektOpptjeningBelop": {
                                        "inntektOpptjeningBelopId": 302319528,
                                        "ar": 2016,
                                        "belop": 275611.0,
                                        "sumPensjonsgivendeInntekt": {
                                            "changeStamp": {
                                                "createdBy": "srvpensjon",
                                                "createdDate": 1586931865857,
                                                "updatedBy": "srvpensjon",
                                                "updatedDate": 1586931866344
                                            },
                                            "inntektId": 585473384,
                                            "fnr": "12117121168",
                                            "inntektAr": 2016,
                                            "kilde": "POPP",
                                            "kommune": null,
                                            "piMerke": null,
                                            "inntektType": "SUM_PI",
                                            "belop": 275611
                                        },
                                        "inntektListe": [
                                            {
                                                "changeStamp": {
                                                    "createdBy": "TESTDATA",
                                                    "createdDate": 1586931865687,
                                                    "updatedBy": "srvpensjon",
                                                    "updatedDate": 1586931865850
                                                },
                                                "inntektId": 585473383,
                                                "fnr": "12117121168",
                                                "inntektAr": 2016,
                                                "kilde": "PEN",
                                                "kommune": "1337",
                                                "piMerke": null,
                                                "inntektType": "INN_LON",
                                                "belop": 275611
                                            }
                                        ],
                                        "changeStamp": {
                                            "createdBy": "srvpensjon",
                                            "createdDate": 1586931866338,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1586931866338
                                        }
                                    },
                                    "omsorgOpptjeningBelop": null,
                                    "dagpengerOpptjeningBelop": null,
                                    "forstegangstjenesteOpptjeningBelop": null,
                                    "uforeOpptjeningBelop": null,
                                    "changeStamp": {
                                        "createdBy": "srvpensjon",
                                        "createdDate": 1586931866338,
                                        "updatedBy": "srvpensjon",
                                        "updatedDate": 1586931866338
                                    }
                                }
                            ]
                        }""");
    }

    private static MockResponse nonExistentPersonResponse() {
        return jsonResponse().setResponseCode(512)
                .setBody("""
                        {
                            "exception": "PersonDoesNotExistExceptionDto",
                            "message": "Person with pid = 01020312345 does not exist"
                        }""");
    }

    private static MockResponse invalidPidResponse() {
        return jsonResponse(INTERNAL_SERVER_ERROR)
                .setBody("""
                        {
                            "message": "Pid validation failed, Pid validation failed, 01020312345 is not a valid personal identification number is not a valid personal identification number"
                        }""");
    }

    private static MockResponse unauthorizedResponse() {
        return jsonResponse(UNAUTHORIZED);
    }
}
