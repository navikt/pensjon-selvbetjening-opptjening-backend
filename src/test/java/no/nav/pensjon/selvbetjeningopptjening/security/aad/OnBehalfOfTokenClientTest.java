package no.nav.pensjon.selvbetjeningopptjening.security.aad;

import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterException;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OnBehalfOfTokenClientTest extends WebClientTest {

    private static final String AUDIENCE = "audience:1";
    private static final String ACCESS_TOKEN = "token1";
    private static final String PID = "pid1";
    private static final TokenAccessParam TOKEN_ACCESS_PARAM = TokenAccessParam.jwtBearer("assertion1");
    private OnBehalfOfTokenClient tokenGetter;

    @Mock
    Oauth2ConfigGetter oauth2ConfigGetter;
    @Mock
    ExpirationChecker expirationChecker;

    @BeforeEach
    void initialize() {
        tokenGetter = new OnBehalfOfTokenClient(oauth2ConfigGetter, expirationChecker, "client1", "secret1");
    }

    @Test
    void when_correct_request_getTokenData_returns_data_containing_accessToken() {
        prepare(tokenResponse());
        when(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        TokenData tokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID);

        assertEquals(ACCESS_TOKEN, tokenData.getAccessToken());
    }

    @Test
    void getTokenData_caches_tokenData() {
        prepare(tokenResponse());
        when(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        TokenData tokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID);
        // Next statement will fail if token not cached, since only one response is enqueued:
        TokenData cachedTokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID);

        assertEquals(ACCESS_TOKEN, tokenData.getAccessToken());
        assertEquals(ACCESS_TOKEN, cachedTokenData.getAccessToken());
    }

    @Test
    void when_bad_request_getTokenData_throws_TokenGetterException() {
        prepare(errorResponse());
        when(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl());
        when(expirationChecker.time()).thenReturn(LocalDateTime.MIN);

        var exception = assertThrows(TokenGetterException.class, () -> tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID));

        assertTrue(exception.getCause() instanceof WebClientResponseException);
        assertTrue(exception.getMessage().startsWith("Failed to obtain token"));
    }

    @Test
    void prepareTokenRequestBody_returns_map_with_targetAppScope() {
        MultiValueMap<String, String> requestBody = tokenGetter.prepareTokenRequestBody(TOKEN_ACCESS_PARAM, AUDIENCE);

        assertEquals(format("api://%s/.default", "audience.1"), requestBody.get("scope").get(0));
        assertEquals("on_behalf_of", requestBody.get("requested_token_use").get(0));
    }

    /**
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow#success-response-example
     */
    private static MockResponse tokenResponse() {
        return jsonResponse(HttpStatus.OK)
                .setBody(format("""
                        {
                          "token_type": "Bearer",
                          "scope": "scope1",
                          "expires_in": 3269,
                          "ext_expires_in": 0,
                          "access_token": "%s",
                          "refresh_token": "refresh-token"
                        }""", ACCESS_TOKEN));
    }

    /**
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow#error-response-example
     */
    private static MockResponse errorResponse() {
        return jsonResponse(HttpStatus.BAD_REQUEST)
                .setBody("""
                        {
                          "error":"interaction_required",
                          "error_description":"AADSTS50079: Due to a configuration change made by your administrator, or because you moved to a new location, you must enroll in multi-factor authentication to access 'bf8d80f9-9098-4972-b203-500f535113b1'.\\r\\nTrace ID: b72a68c3-0926-4b8e-bc35-3150069c2800\\r\\nCorrelation ID: 73d656cf-54b1-4eb2-b429-26d8165a52d7\\r\\nTimestamp: 2017-05-01 22:43:20Z",
                          "error_codes":[50079],
                          "timestamp":"2017-05-01 22:43:20Z",
                          "trace_id":"b72a68c3-0926-4b8e-bc35-3150069c2800",
                          "correlation_id":"73d656cf-54b1-4eb2-b429-26d8165a52d7",
                          "claims":"{\\"access_token\\":{\\"polids\\":{\\"essential\\":true,\\"values\\":[\\"9ab03e19-ed42-4168-b6b7-7001fb3e933a\\"]}}}"
                        }""");
    }
}
