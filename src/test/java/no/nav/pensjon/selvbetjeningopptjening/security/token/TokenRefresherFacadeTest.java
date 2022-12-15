package no.nav.pensjon.selvbetjeningopptjening.security.token;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenRefresher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TokenRefresherFacadeTest {

    private static final String TOKEN = "j.w.t";
    private TokenRefresherFacade tokenRefresherFacade;

    @Mock
    private TokenRefresher tokenRefresherForExternalUsers;
    @Mock
    private TokenRefresher tokenRefresherForInternalUsers;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void initialize() {
        tokenRefresherFacade = new TokenRefresherFacade(
                tokenRefresherForExternalUsers,
                tokenRefresherForInternalUsers);
    }

    @Test
    void refreshToken_returns_refreshToken_for_externalUser() {
        when(tokenRefresherForExternalUsers.refreshToken(request)).thenReturn(tokenData());
        TokenData data = tokenRefresherFacade.refreshToken(UserType.EXTERNAL, request);
        assertEquals(TOKEN, data.getAccessToken());
    }

    @Test
    void refreshToken_returns_refreshToken_for_internalUser() {
        when(tokenRefresherForInternalUsers.refreshToken(request)).thenReturn(tokenData());
        TokenData data = tokenRefresherFacade.refreshToken(UserType.INTERNAL, request);
        assertEquals(TOKEN, data.getAccessToken());
    }

    @Test
    void refreshToken_throwsIllegalArgumentException_for_selfTest() {
        var exception = assertThrows(
                IllegalArgumentException.class, () -> tokenRefresherFacade.refreshToken(UserType.SELF_TEST, request));

        assertEquals("Unsupported user type: SELF_TEST", exception.getMessage());
    }

    private static TokenData tokenData() {
        return new TokenData(TOKEN, "", "", LocalDateTime.MIN, 1L);
    }
}
