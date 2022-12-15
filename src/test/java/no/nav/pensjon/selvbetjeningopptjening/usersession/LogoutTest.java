package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.User;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class LogoutTest {

    private Logout logout;

    @Mock
    private HttpSession session;
    @Mock
    private HttpServletResponse response;
    @Mock
    private EgressAccessTokenFacade egressAccessTokenFacade;
    @Mock
    private CookieSetter cookieSetter;

    @BeforeEach
    void initialize() {
        logout = new Logout(egressAccessTokenFacade, cookieSetter);
    }

    @Test
    void perform_clears_onBehalfOfCookie_when_no_user() {
        logout.perform(response, noUser(), audiences());
        verify(cookieSetter, times(1)).unsetCookie(response, CookieType.ON_BEHALF_OF_PID);
    }

    @Test
    void perform_clearsAccessTokens_when_user_provided() {
        logout.perform(response, user(), audiences());

        verify(egressAccessTokenFacade, times(1)).clearAccessToken("user1", UserType.INTERNAL, "audience1");
        verify(egressAccessTokenFacade, times(1)).clearAccessToken("user1", UserType.INTERNAL, "audience2");
    }

    @Test
    void perform_clears_onBehalfOfCookie_evenIfOtherOperationsFail() {
        doThrow(oops()).when(egressAccessTokenFacade).clearAccessToken(any(), any(), any());
        doThrow(oops()).when(session).invalidate();

        logout.perform(response, user(), audiences());

        verify(cookieSetter, times(1)).unsetCookie(response, CookieType.ON_BEHALF_OF_PID);
    }

    private static User user() {
        return new User("user1", UserType.INTERNAL);
    }

    private static User noUser() {
        return new User("", UserType.NONE);
    }

    private static Set<String> audiences() {
        return Set.of("audience1", "audience2");
    }

    private static RuntimeException oops() {
        return new RuntimeException("oops");
    }
}
