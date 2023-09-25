package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MultiIssuerSupportTest extends WebClientTest {

    private MultiIssuerSupport support;

    @Mock
    private Oauth2BasicData oauth2BasicData1;
    @Mock
    private Oauth2BasicData oauth2BasicData2;

    @BeforeEach
    void initialize() {
        support = new MultiIssuerSupport(oauth2BasicData1, oauth2BasicData2);
    }

    @Test
    void getOauth2HandlerForIssuer() {
        when(oauth2BasicData1.getWellKnownUrl()).thenReturn(baseUrl());
        when(oauth2BasicData1.getAcceptedAudience()).thenReturn("aud1");
        when(oauth2BasicData2.getWellKnownUrl()).thenReturn(baseUrl());
        when(oauth2BasicData2.getAcceptedAudience()).thenReturn("aud2");
        prepare(wellKnownEndpointResponse(1));
        prepare(wellKnownEndpointResponse(2));
        prepare(wellKnownEndpointResponse(3));

        Oauth2Handler handler = support.getOauth2HandlerForIssuer("https://example.provider/1");
        // This call will fail if caching fails:
        Oauth2Handler cachedHandler = support.getOauth2HandlerForIssuer("https://example.provider/1");

        assertEquals("aud1", handler.getAcceptedAudience());
        assertEquals("aud1", cachedHandler.getAcceptedAudience());
    }

    private static MockResponse wellKnownEndpointResponse(int number) {
        return jsonResponse(HttpStatus.OK)
                .setBody(format("""
                        {
                        	"issuer": "https://example.provider/%s",
                        	"authorization_endpoint": "https://example.provider/authorize",
                        	"pushed_authorization_request_endpoint": "https://example.provider/par",
                        	"token_endpoint": "https://example.provider/token",
                        	"end_session_endpoint": "https://example.provider/endsession",
                        	"revocation_endpoint": "https://example.provider/revoke",
                        	"jwks_uri": "https://example.provider/jwk",
                        	"response_types_supported": ["code", "id_token", "id_token token", "token"],
                        	"response_modes_supported": ["query", "form_post", "fragment"],
                        	"subject_types_supported": ["pairwise"],
                        	"id_token_signing_alg_values_supported": ["RS256"],
                        	"code_challenge_methods_supported": ["S256"],
                        	"userinfo_endpoint": "https://example.provider/userinfo",
                        	"scopes_supported": ["openid", "profile"],
                        	"ui_locales_supported": ["nb", "nn", "en", "se"],
                        	"acr_values_supported": ["Level3", "Level4"],
                        	"frontchannel_logout_supported": true,
                        	"frontchannel_logout_session_supported": true,
                        	"introspection_endpoint": "https://example.provider/tokeninfo",
                        	"token_endpoint_auth_methods_supported": ["client_secret_post", "client_secret_basic", "private_key_jwt", "none"],
                        	"request_parameter_supported": true,
                        	"request_uri_parameter_supported": false,
                        	"request_object_signing_alg_values_supported": ["RS256", "RS384", "RS512"]
                        }""", number));
    }
}
