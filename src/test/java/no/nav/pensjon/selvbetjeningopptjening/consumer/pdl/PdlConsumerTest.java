package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserToken;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PdlConsumerTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private static final String BIRTH_DATE = "2001-01-01";
    private static MockWebServer server;
    private static String baseUrl;
    private PdlConsumer consumer;

    @Mock
    TokenValidationContextHolder tokenValidationContextHolder;
    @Mock
    ServiceUserTokenGetter serviceUserTokenGetter;

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
        when(tokenValidationContextHolder.getTokenValidationContext()).thenReturn(tokenValidationContext());
        when(serviceUserTokenGetter.getServiceUserToken()).thenReturn(new ServiceUserToken());
        consumer = new PdlConsumer(baseUrl, tokenValidationContextHolder, serviceUserTokenGetter);
    }

    @Test
    void getBirthDates_shall_return_birthDate_when_one_exists() {
        server.enqueue(pdlDataResponse());

        List<BirthDate> birthDates = consumer.getBirthDates(PID, false);

        assertEquals(1, birthDates.size());
        BirthDate birthDate = birthDates.get(0);
        assertFalse(birthDate.isBasedOnYearOnly());
        assertEquals(LocalDate.of(2001, 1, 1), birthDate.getValue());
    }

    @Test
    void getBirthDates_shall_throwException_when_PDL_returns_error() {
        server.enqueue(pdlErrorResponse());

        FailedCallingExternalServiceException exception = assertThrows(FailedCallingExternalServiceException.class,
                () -> consumer.getBirthDates(PID, false));

        assertEquals("Error when calling the external service PDL. Ikke tilgang til å se person",
                exception.getMessage());
    }

    private static TokenValidationContext tokenValidationContext() {
        Map<String, JwtToken> tokenMap = new HashMap<>();
        tokenMap.put("selvbetjening", new JwtToken(jwt()));
        return new TokenValidationContext(tokenMap);
    }

    private static String jwt() {
        return "eyJraWQiOiJkZWZjZTlkYi05NTk0LTRhOTUtYThjOS1lNTZiZmY2ZDlmYmMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9" +
                ".eyJzdWIiOiJzcnZwZW5zam9uIiwiYXVkIjpbInNydnBlbnNqb24iLCJwcmVwcm9kLmxvY2FsIl0sInZlciI6IjEuMCIsIm5iZiI6MTYwMDk0NTAxNiwiYXpwIjoic3J2cGVuc2pvbiIsImlkZW50VHlwZSI6IlN5c3RlbXJlc3N1cnMiLCJhdXRoX3RpbWUiOjE2MDA5NDUwMTYsImlzcyI6Imh0dHBzOlwvXC9zZWN1cml0eS10b2tlbi1zZXJ2aWNlLm5haXMucHJlcHJvZC5sb2NhbCIsImV4cCI6MTYwMDk0ODYxNiwiaWF0IjoxNjAwOTQ1MDE2LCJqdGkiOiI2MDU3Mzc3MS04MTZhLTQxNjYtOWQ2Yi1hYTYxMGFmZDIwNmYifQ" +
                ".dFjzDGxwPbNvHVykflTC5v6Kl44LljoXMjPyxvKY5I6fGNVRUU6N9nzjVycog0IqPcNESeRjP3iw7Js7Rpns-hkaS5d1IhTqo0mWtr5fJ65mNkD9vf3tmGQYGVLmOl5MW8ySqSyiUeZXsD8JVm7inqKCfOpShwm8jNV2wikW3pfmI--vnsVdOTAtkFFCXMwzhLgsioFb9ajBBn5MYJ3Z87WH_2u40RGRc5vpvVf8Jc5KQXOY5LT_k_HgC1JklO4-AIfUsJlolx-KRlw_62nPLA1R3hZDfYkvBi-zU4yeVgLGH9IdlFK3sT2oIJTSUwiOLazTAOEL7MnX1gvDzz6anA";
    }

    private static MockResponse pdlDataResponse() {
        return new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("{\n" +
                        "  \"data\": {\n" +
                        "    \"hentPerson\": {\n" +
                        "      \"foedsel\": [{\n" +
                        "        \"foedselsaar\": \"null\",\n" +
                        "        \"foedselsdato\": \"" + BIRTH_DATE + "\"\n" +
                        "      }]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    private static MockResponse pdlErrorResponse() {
        return new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("{\n" +
                        "  \"errors\": [{\n" +
                        "    \"message\": \"Ikke tilgang til å se person\",\n" +
                        "    \"locations\": [{\n" +
                        "      \"line\": 1,\n" +
                        "      \"column\": 20\n" +
                        "    }],\n" +
                        "    \"extensions\": {\n" +
                        "      \"code\": \"unauthorized\",\n" +
                        "      \"classification\": \"ExecutionAborted\"\n" +
                        "    },\n" +
                        "    \"path\": [\"hentPerson\"]\n" +
                        "  }]\n" +
                        "}");
    }
}
