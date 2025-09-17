package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;
/*
import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
//import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class OpptjeningsgrunnlagConsumerTest extends WebClientTest {

    private static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
    private static final String MOCK_FNR = "01020312345";
    private OpptjeningsgrunnlagConsumer consumer;

    private static final String EXPECTED_GENERAL_ERROR_MESSAGE = "Error when calling the external service " +
            CONSUMED_SERVICE + " in " + POPP + ".";

    @BeforeEach
    void initialize() {
        consumer = new OpptjeningsgrunnlagConsumer(WebClient.create(), baseUrl());
    }

    @Test
    void should_return_listOfInntekt_when_getInntektListeFromOpptjeningsgrunnlag() throws Exception {
        prepare(okResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSOPPTJENING_REGISTER.appName)) {
            List<Inntekt> inntekter = consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018);

            RecordedRequest request = takeRequest();
            HttpUrl requestUrl = request.getRequestUrl();
            assertNotNull(requestUrl);
            assertEquals("GET", request.getMethod());
            assertEquals("Bearer token2", request.getHeader(HttpHeaders.AUTHORIZATION));
            assertEquals("fomAr", requestUrl.queryParameterName(0));
            assertEquals("2017", requestUrl.queryParameterValue(0));
            assertEquals("tomAr", requestUrl.queryParameterName(1));
            assertEquals("2018", requestUrl.queryParameterValue(1));
            assertEquals(2, inntekter.size());
            assertInntekt(inntekter.get(0), 2017, "INN_LON", 280241L);
            assertInntekt(inntekter.get(1), 2018, "SUM_PI", 280242L);
        }
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_unauthorized() {
        prepare(unauthorizedResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSOPPTJENING_REGISTER.appName)) {
            var thrown = assertThrows(
                    FailedCallingExternalServiceException.class,
                    () -> consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018));

            assertThat(thrown.getMessage(), is(EXPECTED_GENERAL_ERROR_MESSAGE + " Received 401 UNAUTHORIZED"));
        }
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_nonExistentPerson() {
        prepare(nonExistentPersonResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSOPPTJENING_REGISTER.appName)) {
            var thrown = assertThrows(
                    FailedCallingExternalServiceException.class,
                    () -> consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018));

            assertThat(thrown.getMessage(), is(EXPECTED_GENERAL_ERROR_MESSAGE + " Person ikke funnet"));
        }
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_invalidPid() {
        prepare(invalidPidResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PENSJONSOPPTJENING_REGISTER.appName)) {
            var thrown = assertThrows(
                    FailedCallingExternalServiceException.class,
                    () -> consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018));

            assertThat(thrown.getMessage(),
                    is(EXPECTED_GENERAL_ERROR_MESSAGE +
                            " An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
        }
    }

    private static void assertInntekt(Inntekt actual, int expectedYear, String expectedType, long expectedBelop) {
        assertEquals(expectedYear, actual.getYear());
        assertEquals(expectedType, actual.getType());
        assertEquals(expectedBelop, actual.getBelop());
    }

    private static MockResponse okResponse() {
        return jsonResponse()
                .setBody("""
                        {
                            "opptjeningsGrunnlag": {
                                "fnr": "12117121168",
                                "inntektListe": [
                                    {
                                        "changeStamp": {
                                            "createdBy": "TESTDATA",
                                            "createdDate": 1586931866460,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1586931866775
                                        },
                                        "inntektId": 585473583,
                                        "fnr": "12117121168",
                                        "inntektAr": 2017,
                                        "kilde": "PEN",
                                        "kommune": "1337",
                                        "piMerke": null,
                                        "inntektType": "INN_LON",
                                        "belop": 280241
                                    },
                                    {
                                        "changeStamp": {
                                            "createdBy": "srvpensjon",
                                            "createdDate": 1586931866782,
                                            "updatedBy": "srvpensjon",
                                            "updatedDate": 1586931866946
                                        },
                                        "inntektId": 585473584,
                                        "fnr": "12117121168",
                                        "inntektAr": 2018,
                                        "kilde": "POPP",
                                        "kommune": null,
                                        "piMerke": null,
                                        "inntektType": "SUM_PI",
                                        "belop": 280242
                                    }
                                ],
                                "omsorgListe": [],
                                "dagpengerListe": [],
                                "forstegangstjeneste": null
                            }
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
*/
