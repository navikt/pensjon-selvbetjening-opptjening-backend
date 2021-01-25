package no.nav.pensjon.selvbetjeningopptjening.consumer.msgraph;

import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup.OKONOMI;
import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup.VEILEDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

class MicrosoftGraphConsumerTest extends WebClientTest {

    private static final List<AadGroup> GROUPS = List.of(VEILEDER, OKONOMI);
    private static final String TOKEN = "token";
    private MicrosoftGraphConsumer consumer;

    @BeforeEach
    void initialize() {
        consumer = new MicrosoftGraphConsumer(WebClient.create(), baseUrl());
    }

    @Test
    void checkMemberGroups_returnsGroups_when_userAuthorized() {
        prepare(okResponse());

        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);

        assertEquals(2, groups.size());
        assertEquals(VEILEDER, groups.get(0));
        assertEquals(OKONOMI, groups.get(1));
    }

    @Test
    void checkMemberGroups_returnsNoGroups_when_expiredToken() {
        prepare(expiredTokenResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void checkMemberGroups_returnsNoGroups_when_missingToken() {
        prepare(missingTokenResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void checkMemberGroups_returnsNoGroups_when_tokenCannotBeParsed() {
        prepare(parsingFailureTokenResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void checkMemberGroups_returnsNoGroups_when_methodNotAllowed() {
        prepare(methodNotAllowedResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    @Test
    void checkMemberGroups_returnsNoGroups_when_invalidGroupId() {
        prepare(invalidGroupIdResponse());
        List<AadGroup> groups = consumer.checkMemberGroups(GROUPS, TOKEN);
        assertEquals(0, groups.size());
    }

    private static MockResponse okResponse() {
        return jsonResponse()
                .setBody("{\n" +
                        "  \"@odata.context\": \"https://graph.microsoft.com/v1.0/$metadata#Collection(Edm.String)\",\n" +
                        "  \"value\": [\"959ead5b-99b5-466b-a0ff-5fdbc687517b\", \"70ef8e7f-7456-4298-95e0-b13c0ef2422b\"]\n" +
                        "}");
    }

    private static MockResponse expiredTokenResponse() {
        return jsonResponse(UNAUTHORIZED)
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
        return jsonResponse(UNAUTHORIZED)
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
        return jsonResponse(UNAUTHORIZED)
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
        return jsonResponse(METHOD_NOT_ALLOWED)
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
        return jsonResponse(BAD_REQUEST)
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
