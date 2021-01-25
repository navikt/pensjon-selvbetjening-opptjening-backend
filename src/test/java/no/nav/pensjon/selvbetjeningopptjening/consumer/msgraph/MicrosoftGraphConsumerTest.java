package no.nav.pensjon.selvbetjeningopptjening.consumer.msgraph;

import no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicrosoftGraphConsumerTest {

    private static final List<AadGroup> GROUPS = List.of(AadGroup.VEILEDER, AadGroup.OKONOMI);
    private static final String TOKEN = "token";
    private static MockWebServer server;
    private static String baseUrl;
    private MicrosoftGraphConsumer consumer;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = String.format("http://localhost:%s", server.getPort());
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void initialize() {
        consumer = new MicrosoftGraphConsumer(WebClient.create(), baseUrl);
    }

    @Test
    void getMemberGroups_returnsGroups_when_userAuthorized() {
        server.enqueue(okResponse());

        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);

        assertEquals(2, groups.size());
        assertEquals(AadGroup.VEILEDER, groups.get(0));
        assertEquals(AadGroup.OKONOMI, groups.get(1));
    }

    @Test
    void getMemberGroups_returnsNoGroups_when_expiredToken() {
        server.enqueue(expiredTokenResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void getMemberGroups_returnsNoGroups_when_missingToken() {
        server.enqueue(missingTokenResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void getMemberGroups_returnsNoGroups_when_tokenCannotBeParsed() {
        server.enqueue(parsingFailureTokenResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void getMemberGroups_returnsNoGroups_when_methodNotAllowed() {
        server.enqueue(methodNotAllowedResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void getMemberGroups_returnsNoGroups_when_invalidGroupId() {
        server.enqueue(invalidGroupIdResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    private static MockResponse okResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody("{\n" +
                        "  \"@odata.context\": \"https://graph.microsoft.com/v1.0/$metadata#Collection(Edm.String)\",\n" +
                        "  \"value\": [\"959ead5b-99b5-466b-a0ff-5fdbc687517b\", \"70ef8e7f-7456-4298-95e0-b13c0ef2422b\"]\n" +
                        "}");
    }

    private static MockResponse expiredTokenResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
                .setBody("{\n" +
                        "  \"error\": {\n" +
                        "    \"code\": \"InvalidAuthenticationToken\",\n" +
                        "    \"message\": \"Access token has expired.\",\n" +
                        "    \"innerError\": {\n" +
                        "      \"date\": \"2021-01-15T16:07:05\",\n" +
                        "      \"request-id\": \"28e8f23e-e458-4eb1-b4c4-482a65a74abb\",\n" +
                        "      \"client-request-id\": \"28e8f23e-e458-4eb1-b4c4-482a65a74abb\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    private static MockResponse missingTokenResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
                .setBody("{\n" +
                        "  \"error\": {\n" +
                        "    \"code\": \"InvalidAuthenticationToken\",\n" +
                        "    \"message\": \"Access token validation failure.\",\n" +
                        "    \"innerError\": {\n" +
                        "      \"date\": \"2021-01-15T17:31:39\",\n" +
                        "      \"request-id\": \"4e9ccd55-3b5c-4192-8470-bd9972ae40d7\",\n" +
                        "      \"client-request-id\": \"4e9ccd55-3b5c-4192-8470-bd9972ae40d7\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    private static MockResponse parsingFailureTokenResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
                .setBody("{\n" +
                        "  \"error\": {\n" +
                        "    \"code\": \"InvalidAuthenticationToken\",\n" +
                        "    \"message\": \"CompactToken parsing failed with error code: 80049217\",\n" +
                        "    \"innerError\": {\n" +
                        "      \"date\": \"2021-01-15T17:12:49\",\n" +
                        "      \"request-id\": \"aed581e1-c6b7-4192-8a3f-f22e8ce53895\",\n" +
                        "      \"client-request-id\": \"aed581e1-c6b7-4192-8a3f-f22e8ce53895\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    private static MockResponse methodNotAllowedResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .setBody("{\n" +
                        "  \"error\": {\n" +
                        "    \"code\": \"Request_BadRequest\",\n" +
                        "    \"message\": \"Specified HTTP method is not allowed for the request target.\",\n" +
                        "    \"innerError\": {\n" +
                        "      \"date\": \"2021-01-15T17:19:59\",\n" +
                        "      \"request-id\": \"5c907b95-6335-4ea2-a8d2-8f6bf0738a7a\",\n" +
                        "      \"client-request-id\": \"5c907b95-6335-4ea2-a8d2-8f6bf0738a7a\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    private static MockResponse invalidGroupIdResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setBody("{\n" +
                        "  \"error\": {\n" +
                        "    \"code\": \"Request_BadRequest\",\n" +
                        "    \"message\": \"Invalid GUID:-2d6c-4418-8b1e-4d229bb2001c\",\n" +
                        "    \"innerError\": {\n" +
                        "      \"date\": \"2021-01-15T17:22:35\",\n" +
                        "      \"request-id\": \"3f259233-411f-42be-b812-199956630fd0\",\n" +
                        "      \"client-request-id\": \"3f259233-411f-42be-b812-199956630fd0\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }
}
