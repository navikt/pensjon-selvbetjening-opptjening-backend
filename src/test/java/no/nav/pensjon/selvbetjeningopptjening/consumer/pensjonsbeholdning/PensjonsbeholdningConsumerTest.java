package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserToken;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.StsException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@TestPropertySource(properties = "fnr=dummy")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PensjonsbeholdningConsumerTest extends WebClientTest {

    private static final String CONSUMED_SERVICE = "PROPOPP006 hentPensjonsbeholdningListe";
    private static final ServiceUserToken TOKEN = new ServiceUserToken("token", 1L, "type");
    private PensjonsbeholdningConsumer consumer;

    private static final String EXPECTED_GENERAL_ERROR_MESSAGE = "Error when calling the external service " +
            CONSUMED_SERVICE + " in " + POPP + ".";

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceUserTokenGetter tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenGetter.getServiceUserToken()).thenReturn(TOKEN);
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
        /* Probably susceptible to time-zone issues:
        assertEquals(LocalDate.of(1994, 1, 1), beholdning.getFomDato());
        assertEquals(LocalDate.of(1994, 12, 31), beholdning.getTomDato());
        */
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
                .setBody("{\n" +
                        "    \"beholdninger\": [\n" +
                        "        {\n" +
                        "            \"beholdningId\": 430456711,\n" +
                        "            \"fnr\": \"12117121168\",\n" +
                        "            \"status\": \"G\",\n" +
                        "            \"beholdningType\": \"PEN_B\",\n" +
                        "            \"belop\": 20137.460428429236,\n" +
                        "            \"vedtakId\": null,\n" +
                        "            \"fomDato\": 757378800000,\n" +
                        "            \"tomDato\": 788828400000,\n" +
                        "            \"beholdningGrunnlag\": 108655.1,\n" +
                        "            \"beholdningGrunnlagAvkortet\": 108655.0,\n" +
                        "            \"beholdningInnskudd\": 20137.460428429236,\n" +
                        "            \"beholdningInnskuddUtenOmsorg\": 19666.555000000004,\n" +
                        "            \"oppdateringArsak\": \"NY_OPPTJENING\",\n" +
                        "            \"lonnsvekstregulering\": null,\n" +
                        "            \"inntektOpptjeningBelop\": {\n" +
                        "                \"inntektOpptjeningBelopId\": 302319479,\n" +
                        "                \"ar\": 1992,\n" +
                        "                \"belop\": 108655.0,\n" +
                        "                \"sumPensjonsgivendeInntekt\": {\n" +
                        "                    \"changeStamp\": {\n" +
                        "                        \"createdBy\": \"srvpensjon\",\n" +
                        "                        \"createdDate\": 1586931809567,\n" +
                        "                        \"updatedBy\": \"srvpensjon\",\n" +
                        "                        \"updatedDate\": 1586931813383\n" +
                        "                    },\n" +
                        "                    \"inntektId\": 585473360,\n" +
                        "                    \"fnr\": \"12117121168\",\n" +
                        "                    \"inntektAr\": 1992,\n" +
                        "                    \"kilde\": \"POPP\",\n" +
                        "                    \"kommune\": null,\n" +
                        "                    \"piMerke\": null,\n" +
                        "                    \"inntektType\": \"SUM_PI\",\n" +
                        "                    \"belop\": 108655\n" +
                        "                },\n" +
                        "                \"inntektListe\": [\n" +
                        "                    {\n" +
                        "                        \"changeStamp\": {\n" +
                        "                            \"createdBy\": \"TESTDATA\",\n" +
                        "                            \"createdDate\": 1586931809059,\n" +
                        "                            \"updatedBy\": \"srvpensjon\",\n" +
                        "                            \"updatedDate\": 1586931809534\n" +
                        "                        },\n" +
                        "                        \"inntektId\": 585473359,\n" +
                        "                        \"fnr\": \"12117121168\",\n" +
                        "                        \"inntektAr\": 1992,\n" +
                        "                        \"kilde\": \"PEN\",\n" +
                        "                        \"kommune\": \"1337\",\n" +
                        "                        \"piMerke\": null,\n" +
                        "                        \"inntektType\": \"INN_LON\",\n" +
                        "                        \"belop\": 108655\n" +
                        "                    }\n" +
                        "                ],\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"srvpensjon\",\n" +
                        "                    \"createdDate\": 1586931854757,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1586931854757\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"omsorgOpptjeningBelop\": null,\n" +
                        "            \"dagpengerOpptjeningBelop\": null,\n" +
                        "            \"forstegangstjenesteOpptjeningBelop\": null,\n" +
                        "            \"uforeOpptjeningBelop\": null,\n" +
                        "            \"changeStamp\": {\n" +
                        "                \"createdBy\": \"srvpensjon\",\n" +
                        "                \"createdDate\": 1586931854758,\n" +
                        "                \"updatedBy\": \"srvpensjon\",\n" +
                        "                \"updatedDate\": 1586931854758\n" +
                        "            }\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"beholdningId\": 430457040,\n" +
                        "            \"fnr\": \"12117121168\",\n" +
                        "            \"status\": \"G\",\n" +
                        "            \"beholdningType\": \"PEN_B\",\n" +
                        "            \"belop\": 1317010.8630999844,\n" +
                        "            \"vedtakId\": null,\n" +
                        "            \"fomDato\": 1525125600000,\n" +
                        "            \"tomDato\": null,\n" +
                        "            \"beholdningGrunnlag\": 275611.0,\n" +
                        "            \"beholdningGrunnlagAvkortet\": 275611.0,\n" +
                        "            \"beholdningInnskudd\": 50913.322334762146,\n" +
                        "            \"beholdningInnskuddUtenOmsorg\": 49885.59100000001,\n" +
                        "            \"oppdateringArsak\": \"REGULERING\",\n" +
                        "            \"lonnsvekstregulering\": {\n" +
                        "                \"lonnsvekstreguleringId\": 318421599,\n" +
                        "                \"reguleringsbelop\": 44167.659176156856,\n" +
                        "                \"reguleringsDato\": 1525125600000,\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"srvpensjon\",\n" +
                        "                    \"createdDate\": 1586931866338,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1586931866338\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"inntektOpptjeningBelop\": {\n" +
                        "                \"inntektOpptjeningBelopId\": 302319528,\n" +
                        "                \"ar\": 2016,\n" +
                        "                \"belop\": 275611.0,\n" +
                        "                \"sumPensjonsgivendeInntekt\": {\n" +
                        "                    \"changeStamp\": {\n" +
                        "                        \"createdBy\": \"srvpensjon\",\n" +
                        "                        \"createdDate\": 1586931865857,\n" +
                        "                        \"updatedBy\": \"srvpensjon\",\n" +
                        "                        \"updatedDate\": 1586931866344\n" +
                        "                    },\n" +
                        "                    \"inntektId\": 585473384,\n" +
                        "                    \"fnr\": \"12117121168\",\n" +
                        "                    \"inntektAr\": 2016,\n" +
                        "                    \"kilde\": \"POPP\",\n" +
                        "                    \"kommune\": null,\n" +
                        "                    \"piMerke\": null,\n" +
                        "                    \"inntektType\": \"SUM_PI\",\n" +
                        "                    \"belop\": 275611\n" +
                        "                },\n" +
                        "                \"inntektListe\": [\n" +
                        "                    {\n" +
                        "                        \"changeStamp\": {\n" +
                        "                            \"createdBy\": \"TESTDATA\",\n" +
                        "                            \"createdDate\": 1586931865687,\n" +
                        "                            \"updatedBy\": \"srvpensjon\",\n" +
                        "                            \"updatedDate\": 1586931865850\n" +
                        "                        },\n" +
                        "                        \"inntektId\": 585473383,\n" +
                        "                        \"fnr\": \"12117121168\",\n" +
                        "                        \"inntektAr\": 2016,\n" +
                        "                        \"kilde\": \"PEN\",\n" +
                        "                        \"kommune\": \"1337\",\n" +
                        "                        \"piMerke\": null,\n" +
                        "                        \"inntektType\": \"INN_LON\",\n" +
                        "                        \"belop\": 275611\n" +
                        "                    }\n" +
                        "                ],\n" +
                        "                \"changeStamp\": {\n" +
                        "                    \"createdBy\": \"srvpensjon\",\n" +
                        "                    \"createdDate\": 1586931866338,\n" +
                        "                    \"updatedBy\": \"srvpensjon\",\n" +
                        "                    \"updatedDate\": 1586931866338\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"omsorgOpptjeningBelop\": null,\n" +
                        "            \"dagpengerOpptjeningBelop\": null,\n" +
                        "            \"forstegangstjenesteOpptjeningBelop\": null,\n" +
                        "            \"uforeOpptjeningBelop\": null,\n" +
                        "            \"changeStamp\": {\n" +
                        "                \"createdBy\": \"srvpensjon\",\n" +
                        "                \"createdDate\": 1586931866338,\n" +
                        "                \"updatedBy\": \"srvpensjon\",\n" +
                        "                \"updatedDate\": 1586931866338\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ]\n" +
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
