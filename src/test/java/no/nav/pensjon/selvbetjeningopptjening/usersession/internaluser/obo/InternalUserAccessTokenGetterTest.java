package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser.obo;

import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class InternalUserAccessTokenGetterTest {

    private static final String ACCESS_TOKEN = "token1";
    private static final String ASSERTION = "assertion1";
    private static final String AUDIENCE = "audience1";
    private static final String PID = "pid1";
    private AccessTokenGetter accessTokenGetter;

    @Mock
    private CacheAwareTokenClient tokenGetter;

    @BeforeEach
    void initialize() {
        accessTokenGetter = new InternalUserAccessTokenGetter(tokenGetter);
    }

    @Test
    void getAccessToken_returns_accessToken() {
        when(tokenGetter.getTokenData(TokenAccessParam.jwtBearer(ASSERTION), AUDIENCE, PID)).thenReturn(tokenData());
        RawJwt accessToken = accessTokenGetter.getAccessToken(ASSERTION, AUDIENCE, PID);
        assertEquals(ACCESS_TOKEN, accessToken.getValue());
    }

    @Test
    void clearAccessToken_clears_accessToken() {
        accessTokenGetter.clearAccessToken(AUDIENCE, PID);
        verify(tokenGetter, times(1)).clearTokenData(AUDIENCE, PID);
    }

    private static TokenData tokenData() {
        return new TokenData(ACCESS_TOKEN,
                "id-token",
                "refresh-token",
                LocalDateTime.MIN,
                1L);
    }
}
