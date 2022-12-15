package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EgressAccessTokenFacadeTest {

    private static final String AUDIENCE = "audience1";
    private static final String EGRESS_TOKEN = "token-out";
    private EgressAccessTokenFacade facade;

    @Mock
    private AccessTokenGetter accessTokenGetterForClientCredentials;

    @BeforeEach
    void initialize() {
        facade = new EgressAccessTokenFacade(null, null, accessTokenGetterForClientCredentials);
    }

    @Test
    void getAccessToken_returns_accessToken_for_application() {
        arrange(accessTokenGetterForClientCredentials);
        RawJwt token = facade.getAccessToken("token1", "pid1", UserType.APPLICATION, AUDIENCE);
        assertEquals(EGRESS_TOKEN, token.getValue());
    }

    private static void arrange(AccessTokenGetter tokenGetter) {
        when(tokenGetter.getAccessToken("token1", AUDIENCE, "pid1")).thenReturn(new RawJwt(EGRESS_TOKEN));
    }
}
