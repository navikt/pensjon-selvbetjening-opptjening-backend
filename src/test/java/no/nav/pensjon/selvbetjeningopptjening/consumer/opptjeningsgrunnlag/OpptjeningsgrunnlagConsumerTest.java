package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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
        consumer = new OpptjeningsgrunnlagConsumer(baseUrl());
    }

    @Test
    void should_return_listOfInntekt_when_getInntektListeFromOpptjeningsgrunnlag() throws InterruptedException {
        prepare(okResponse());

        List<Inntekt> inntekter = consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018);

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("fomAr", requestUrl.queryParameterName(0));
        assertEquals("2017", requestUrl.queryParameterValue(0));
        assertEquals("tomAr", requestUrl.queryParameterName(1));
        assertEquals("2018", requestUrl.queryParameterValue(1));
        assertEquals(2, inntekter.size());
        assertInntekt(inntekter.get(0), 2017, "INN_LON", 280241L);
        assertInntekt(inntekter.get(1), 2018, "SUM_PI", 280242L);
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_401() {
        prepare(unauthorizedResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018));

        assertThat(thrown.getMessage(), is(EXPECTED_GENERAL_ERROR_MESSAGE + " Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_nonExistentPerson() {
        prepare(nonExistentPersonResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018));

        assertThat(thrown.getMessage(), is(EXPECTED_GENERAL_ERROR_MESSAGE + " Person ikke funnet"));
    }


    @Test
    void should_return_FailedCallingExternalServiceException_when_invalidPid() {
        prepare(invalidPidResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag(MOCK_FNR, 2017, 2018));

        assertThat(thrown.getMessage(),
                is(EXPECTED_GENERAL_ERROR_MESSAGE +
                        " An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    private static void assertInntekt(Inntekt actual, int expectedYear, String expectedType, long expectedBelop) {
        assertEquals(expectedYear, actual.getYear());
        assertEquals(expectedType, actual.getType());
        assertEquals(expectedBelop, actual.getBelop());
    }

    private static MockResponse okResponse() {
        return jsonResponse()
                .setBody("{\n" +
                        "    \"opptjeningsGrunnlag\": {\n" +
                        "        \"fnr\": \"12117121168\",\n" +
                        "        \"inntektListe\": [\n" +
                        "            {\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"TESTDATA\",\n" +
                        "                    \"createdDate\": 1586931866460,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1586931866775\n" +
                        "                },\n" +
                        "                \"inntektId\": 585473583,\n" +
                        "                \"fnr\": \"12117121168\",\n" +
                        "                \"inntektAr\": 2017,\n" +
                        "                \"kilde\": \"PEN\",\n" +
                        "                \"kommune\": \"1337\",\n" +
                        "                \"piMerke\": null,\n" +
                        "                \"inntektType\": \"INN_LON\",\n" +
                        "                \"belop\": 280241\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"srvpensjon\",\n" +
                        "                    \"createdDate\": 1586931866782,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1586931866946\n" +
                        "                },\n" +
                        "                \"inntektId\": 585473584,\n" +
                        "                \"fnr\": \"12117121168\",\n" +
                        "                \"inntektAr\": 2018,\n" +
                        "                \"kilde\": \"POPP\",\n" +
                        "                \"kommune\": null,\n" +
                        "                \"piMerke\": null,\n" +
                        "                \"inntektType\": \"SUM_PI\",\n" +
                        "                \"belop\": 280242\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"omsorgListe\": [],\n" +
                        "        \"dagpengerListe\": [],\n" +
                        "        \"forstegangstjeneste\": null\n" +
                        "    }\n" +
                        "}");
    }

    private static MockResponse nonExistentPersonResponse() {
        return jsonResponse().setResponseCode(512)
                .setBody("{\n" +
                        "    \"exception\": \"PersonDoesNotExistExceptionDto\",\n" +
                        "    \"message\": \"Person with pid = 01020312345 does not exist\"\n" +
                        "}");
    }

    private static MockResponse invalidPidResponse() {
        return jsonResponse(INTERNAL_SERVER_ERROR)
                .setBody("{\n" +
                        "    \"message\": \"Pid validation failed, Pid validation failed," +
                        " 01020312345 is not a valid personal identification number" +
                        " is not a valid personal identification number\"\n" +
                        "}");
    }

    private static MockResponse unauthorizedResponse() {
        return jsonResponse(UNAUTHORIZED);
    }
}
