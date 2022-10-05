package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InternalUserTokenGetterTest extends WebClientTest {

    private static final String ID_TOKEN = "id-token";
    private WebClient webClient;

    @Mock
    private OidcConfigGetter oidcConfigGetter;

    @BeforeEach
    void initialize() {
        webClient = WebClient.create();
    }

    @Test
    void getTokenData_returns_tokenData() {
        prepareResponse(tokenResponse());
        when(oidcConfigGetter.getTokenEndpoint()).thenReturn(baseUrl());
        var tokenGetter = new InternalUserTokenGetter(webClient, oidcConfigGetter, "client-id", "client-secret", "redirect-uri");

        TokenData tokenData = tokenGetter.getTokenData(TokenAccessParam.authorizationCode("code"), "");

        assertEquals(ID_TOKEN, tokenData.getIdToken());
    }

    private static String tokenResponse() {
        return "{\n" +
                "  \"access_token\": \"access-token\",\n" +
                "  \"token_type\": \"Bearer\",\n" +
                "  \"expires_in\": 3599,\n" +
                "  \"scope\": \"https%3A%2F%2Fgraph.microsoft.com%2Fmail.read\",\n" +
                "  \"refresh_token\": \"refresh-token\",\n" +
                "  \"id_token\": \"" + ID_TOKEN + "\"\n" +
                "}";
    }

    private static void prepareResponse(String responseBody) {
        prepare(jsonResponse().setBody(responseBody));
    }
}
