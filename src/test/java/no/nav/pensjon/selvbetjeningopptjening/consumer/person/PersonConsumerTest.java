package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uforeperiode;
import no.nav.pensjon.selvbetjeningopptjening.security.token.ServiceTokenData;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonConsumerTest extends WebClientTest {

    private static final String EXPECTED_AFP_HISTORIKK_IDENTIFIER = "PROPEN2602 getAfphistorikkForPerson";
    private static final String EXPECTED_UFOREHISTORIKK_IDENTIFIER = "PROPEN2603 getUforehistorikkForPerson";
    private static final String EXPECTED_AFP_HISTORIKK_ERROR_MESSAGE = "Error when calling the external service " + EXPECTED_AFP_HISTORIKK_IDENTIFIER + " in PEN.";
    private static final String EXPECTED_UFORE_HISTORIKK_ERROR_MESSAGE = "Error when calling the external service " + EXPECTED_UFOREHISTORIKK_IDENTIFIER + " in PEN.";
    private static final ServiceTokenData TOKEN = new ServiceTokenData("token", "type", LocalDateTime.MIN, 1L);
    private PersonConsumer consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @Mock
    ServiceTokenGetter tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenGetter.getServiceUserToken()).thenReturn(TOKEN);
        consumer = new PersonConsumer(webClient, baseUrl(), tokenGetter);
    }

    @Test
    @Order(1)
    void getUforeHistorikkForPerson_returns_uforeHistorikk_when_ok() throws InterruptedException {
        prepare(uforeHistorikkResponse());

        UforeHistorikk historikk = consumer.getUforeHistorikkForPerson("fnr");

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals("fnr", request.getHeader("pid"));
        List<String> segments = requestUrl.pathSegments();
        assertEquals("person", segments.get(0));
        assertEquals("uforehistorikk", segments.get(1));
        List<Uforeperiode> perioder = historikk.getUforeperioder();
        assertEquals(1, perioder.size());
        Uforeperiode periode = perioder.get(0);
        assertEquals(100, periode.getUforegrad());
        assertEquals(UforeTypeCode.UFORE, periode.getUforetype());
        assertEquals(LocalDate.of(2019, 10, 1), periode.getFomDate());
        assertFalse(periode.hasTomDate());
        assertNull(periode.getTomDate());
    }

    @Test
    @Order(4)
    void getUforeHistorikkForPerson_throws_FailedCallingExternalServiceException_when_invalidPid() {
        prepare(invalidPidResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson("fnr"));

        assertThat(thrown.getMessage(),
                is(EXPECTED_UFORE_HISTORIKK_ERROR_MESSAGE + " Received 400 BAD REQUEST"));
    }

    @Test
    @Order(2)
    void getAfpHistorikkForPerson_returns_null_when_noData() throws InterruptedException {
        prepare(emptyResponse());

        AfpHistorikk historikk = consumer.getAfpHistorikkForPerson("fnr");

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals("fnr", request.getHeader("pid"));
        List<String> segments = requestUrl.pathSegments();
        assertEquals("person", segments.get(0));
        assertEquals("afphistorikk", segments.get(1));
        assertNull(historikk);
    }

    @Test
    @Order(3)
    void ping_ok() throws InterruptedException {
        prepare(pingResponse());

        consumer.ping();

        RecordedRequest request = takeRequest();
        HttpUrl requestUrl = request.getRequestUrl();
        assertNotNull(requestUrl);
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader(HttpHeaders.AUTHORIZATION));
        List<String> segments = requestUrl.pathSegments();
        assertEquals("person", segments.get(0));
        assertEquals("ping", segments.get(1));
    }

    @Test
    @Order(5)
    void getUforeHistorikkForPerson_throws_FailedCallingExternalServiceException_when_expiredToken() {
        prepare(expiredTokenResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson("fnr"));

        assertThat(thrown.getMessage(),
                is(EXPECTED_UFORE_HISTORIKK_ERROR_MESSAGE +
                        " An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    @Order(6)
    void getUforeHistorikkForPerson_throws_FailedCallingExternalServiceException_when_invalidToken() {
        prepare(invalidTokenResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson("fnr"));

        assertThat(thrown.getMessage(),
                is(EXPECTED_UFORE_HISTORIKK_ERROR_MESSAGE +
                        " An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Order(7)
    @Test
    void getAfpHistorikkForPerson_throws_FailedCallingExternalServiceException_when_invalidPid() {
        prepare(invalidPidResponse());

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson("fnr"));

        assertThat(thrown.getMessage(),
                is(EXPECTED_AFP_HISTORIKK_ERROR_MESSAGE + " Received 400 BAD REQUEST"));
    }

    private static MockResponse uforeHistorikkResponse() {
        // Based on actual response from PEN
        return jsonResponse()
                .setBody("{\n" +
                        "    \"garantigradYrke\": null,\n" +
                        "    \"reaktiviseringFomDato\": null,\n" +
                        "    \"uforeperiodeListe\": [\n" +
                        "        {\n" +
                        "            \"uforegrad\": 100,\n" +
                        "            \"uforeperiodeId\": 500326589,\n" +
                        "            \"uforetidspunkt\": 1569880800000,\n" +
                        "            \"virk\": 1580511600000,\n" +
                        "            \"uforetype\": \"UFORE\",\n" +
                        "            \"fppGarantiKode\": null,\n" +
                        "            \"redusertAntFppAr\": 0,\n" +
                        "            \"fpp\": 0.0,\n" +
                        "            \"uforetidspunktTom\": null,\n" +
                        "            \"fppGaranti\": 0.0,\n" +
                        "            \"fpp_omregnet\": 0.0,\n" +
                        "            \"spt_pa_f92\": 0,\n" +
                        "            \"spt_pa_e91\": 0,\n" +
                        "            \"opt_pa_f92\": 0,\n" +
                        "            \"opt_pa_e91\": 0,\n" +
                        "            \"ypt_pa_f92\": 0,\n" +
                        "            \"ypt_pa_e91\": 0,\n" +
                        "            \"spt_eos\": 0.0,\n" +
                        "            \"spt_pa_e91_eos\": 0,\n" +
                        "            \"spt_pa_f92_eos\": 0,\n" +
                        "            \"spt\": 0.0,\n" +
                        "            \"opt\": 0.0,\n" +
                        "            \"ypt\": 0.0,\n" +
                        "            \"paa\": 0.0,\n" +
                        "            \"ufgFom\": 1569880800000,\n" +
                        "            \"ufgTom\": null,\n" +
                        "            \"proRataNevner\": null,\n" +
                        "            \"proRataTeller\": null,\n" +
                        "            \"proRataBeregningType\": null,\n" +
                        "            \"redusertAntFppArProRata\": null,\n" +
                        "            \"sptProRata\": null,\n" +
                        "            \"fodselsArYngsteBarn\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"lonnstilskuddTomDato\": null,\n" +
                        "    \"frysHvilPensjTomDato\": null,\n" +
                        "    \"forlengelseFrysTomDato\": null,\n" +
                        "    \"uforehistorikkId\": 557901588,\n" +
                        "    \"garantigrad\": null,\n" +
                        "    \"lonnstilskuddFomDato\": null,\n" +
                        "    \"frysHvilPensjFomDato\": null,\n" +
                        "    \"reaktiviseringTomDato\": null,\n" +
                        "    \"ungUfor\": false,\n" +
                        "    \"forlengelseFrysFomDato\": null\n" +
                        "}");
    }

    private static MockResponse invalidPidResponse() {
        // Actual response from PEN
        return jsonResponse(BAD_REQUEST)
                .setBody("{\n" +
                        "    \"feil\": \"SimplePidParamConverter: Invalid pid as input to REST service\"\n" +
                        "}");
    }

    private static MockResponse expiredTokenResponse() {
        // Based on actual response from PEN
        return plaintextResponse()
                .setResponseCode(INTERNAL_SERVER_ERROR.value())
                .setBody("<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "  <title>HTTP Status 500 – Internal Server Error</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <h1>HTTP Status 500 – Internal Server Error</h1>\n" +
                        "  <p><b>Type</b> Exception Report</p>\n" +
                        "  <p><b>Message</b> JWT token is not valid: JWT\n" +
                        "    (claims-&gt;{&quot;sub&quot;:&quot;srvpensjon&quot;,&quot;aud&quot;:[&quot;srvpensjon&quot;,&quot;preprod.local&quot;],&quot;ver&quot;:&quot;1.0&quot;,&quot;nbf&quot;:1612199803,&quot;azp&quot;:&quot;srvpensjon&quot;,&quot;identType&quot;:&quot;Systemressurs&quot;,&quot;auth_time&quot;:1612199803,&quot;iss&quot;:&quot;https:\\&#47;\\&#47;security-token-service.nais.preprod.local&quot;,&quot;exp&quot;:1612203403,&quot;iat&quot;:1612199803,&quot;jti&quot;:&quot;b6307b4b-db49-4b35-86f7-4d1b8c83d63e&quot;})\n" +
                        "    rejected due to invalid claims. Additional details: [[1] The JWT is no longer valid - the evaluation time\n" +
                        "    NumericDate{1612215217 -&gt; Feb 1, 2021 10:33:37 PM CET} is on or after the Expiration Time\n" +
                        "    (exp=NumericDate{1612203403 -&gt; Feb 1, 2021 7:16:43 PM CET}) claim value (even when providing 20 seconds of\n" +
                        "    leeway to account for clock skew).]</p>\n" +
                        "  <p><b>Description</b> The server encountered an unexpected condition that prevented it from fulfilling the request.\n" +
                        "  </p>\n" +
                        "</body>\n" +
                        "</html>");
    }

    private static MockResponse invalidTokenResponse() {
        // Based on actual response from PEN
        return plaintextResponse()
                .setResponseCode(INTERNAL_SERVER_ERROR.value())
                .setBody("<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "<body>\n" +
                        "  <h1>HTTP Status 500 – Internal Server Error</h1>\n" +
                        "  <p><b>Type</b> Exception Report</p>\n" +
                        "  <p><b>Message</b> JWT token is not valid: JWT rejected due to invalid signature. Additional details: [[9] Invalid\n" +
                        "    JWS Signature:\n" +
                        "    JsonWebSignature{&quot;kid&quot;:&quot;758677f4-88bb-4400-a0d0-e7c8a8325037&quot;,&quot;typ&quot;:&quot;JWT&quot;,&quot;alg&quot;:&quot;RS256&quot;}-&gt;eyJ...6vD]\n" +
                        "  </p>\n" +
                        "  <p><b>Description</b> The server encountered an unexpected condition that prevented it from fulfilling the request.\n" +
                        "  </p>\n" +
                        "</body>\n" +
                        "</html>");
    }

    private static MockResponse pingResponse() {
        // Actual response from PEN
        return plaintextResponse()
                .setBody("Service online!");
    }

    private static MockResponse emptyResponse() {
        return jsonResponse()
                .setBody("");
    }
}
