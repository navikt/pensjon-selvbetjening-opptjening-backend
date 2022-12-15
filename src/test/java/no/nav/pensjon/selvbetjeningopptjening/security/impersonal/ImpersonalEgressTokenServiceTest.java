package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ImpersonalEgressTokenServiceTest {

    private static final String AUDIENCE_1 = "cluster1:namespace1:app1";
    private static final String AUDIENCE_2 = "cluster2:namespace2:app2";
    private static final String APP_ID_1 = "app1";
    private static final String APP_ID_2 = "app2";
    private static final String APP_ID_3 = "app3";
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";

    @Mock
    private EgressAccessTokenFacade accessTokenFacade;

    @Test
    void getEgressAccessTokensByApp_returns_map_of_tokens_by_app() {
        arrangeAccessToken(AUDIENCE_1, TOKEN_1);
        arrangeAccessToken(AUDIENCE_2, TOKEN_2);
        var getter = new ImpersonalEgressTokenService(accessTokenFacade, audiencesVsApps());

        Map<String, Supplier<RawJwt>> tokenSuppliersByApp = getter.getEgressTokenSuppliersByApp();

        assertEquals(3, tokenSuppliersByApp.size());
        assertEquals(TOKEN_1, tokenSuppliersByApp.get(APP_ID_1).get().getValue());
        assertEquals(TOKEN_1, tokenSuppliersByApp.get(APP_ID_2).get().getValue());
        assertEquals(TOKEN_2, tokenSuppliersByApp.get(APP_ID_3).get().getValue());
    }

    private void arrangeAccessToken(String audience, String token) {
        when(accessTokenFacade.getAccessToken(UserType.APPLICATION, audience)).thenReturn(new RawJwt(token));
    }

    private static TokenAudiencesVsApps audiencesVsApps() {
        return new TokenAudiencesVsApps(Map.of(
                AUDIENCE_1, List.of(APP_ID_1, APP_ID_2),
                AUDIENCE_2, List.of(APP_ID_3)));
    }
}
