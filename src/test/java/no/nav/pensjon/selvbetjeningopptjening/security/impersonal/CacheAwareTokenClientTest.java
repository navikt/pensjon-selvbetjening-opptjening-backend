package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CacheAwareTokenClientTest extends WebClientTest {

    private static final String ACCESS_TOKEN = "token1";
    private static final long EXPIRES_IN = 299L;
    private static final String PID1 = "PID1";
    private static final String AUDIENCE = "audience1";
    private static final TokenAccessParam TOKEN_ACCESS_PARAM = TokenAccessParam.clientCredentials("scope");
    private TestClass tokenGetter;

    @Mock
    private Oauth2ConfigGetter oauth2ConfigGetter;
    @Mock
    private ExpirationChecker expirationChecker;

    @BeforeEach
    void initialize() {
        tokenGetter = new TestClass(WebClient.create(), oauth2ConfigGetter, expirationChecker);
    }

    @Test
    void getTokenData_caches_tokenData() {
        prepare(tokenResponse());
        when(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        TokenData tokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1);
        // Next statement will fail if token not cached, since only one response is enqueued:
        TokenData cachedTokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1);

        assertEquals(ACCESS_TOKEN, tokenData.getAccessToken());
        assertEquals(ACCESS_TOKEN, cachedTokenData.getAccessToken());
    }

    @Test
    void clearTokenData_removes_tokenData_from_cache() {
        prepare(tokenResponse());
        prepare(tokenResponse()); // 2 responses needed since cache is cleared
        when(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        TokenData tokenData1 = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1);
        tokenGetter.clearTokenData(AUDIENCE, PID1);
        TokenData tokenData2 = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1);

        assertNotEquals(tokenData1, tokenData2);
        assertEquals(ACCESS_TOKEN, tokenData1.getAccessToken());
    }

    private static MockResponse tokenResponse() {
        return jsonResponse(HttpStatus.OK)
                .setBody("\n" +
                        "{\n" +
                        "  \"access_token\": \"" + ACCESS_TOKEN + "\",\n" +
                        "  \"issued_token_type\": \"urn:ietf:params:oauth:token-type:access_token\",\n" +
                        "  \"token_type\": \"Bearer\",\n" +
                        "  \"expires_in\": " + EXPIRES_IN + "\n" +
                        "}");
    }

    private static class TestClass extends CacheAwareTokenClient {

        public TestClass(WebClient webClient,
                         Oauth2ConfigGetter oauth2ConfigGetter,
                         ExpirationChecker expirationChecker) {
            super(webClient,
                    oauth2ConfigGetter,
                    expirationChecker);
        }

        @Override
        protected MultiValueMap<String, String> prepareTokenRequestBody(TokenAccessParam accessParam, String audience) {
            return new LinkedMultiValueMap<>();
        }

        @Override
        protected int getCleanupTrigger() {
            return 3;
        }
    }
}
