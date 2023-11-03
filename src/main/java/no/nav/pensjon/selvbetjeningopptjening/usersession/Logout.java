package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.security.filter.User;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Set;

@Component
public class Logout {

    private static final Logger log = LoggerFactory.getLogger(Logout.class);
    private final EgressAccessTokenFacade egressAccessTokenFacade;
    private final CookieSetter cookieSetter;

    public Logout(EgressAccessTokenFacade egressAccessTokenFacade, CookieSetter cookieSetter) {
        this.egressAccessTokenFacade = egressAccessTokenFacade;
        this.cookieSetter = cookieSetter;
    }

    public void perform(HttpServletResponse response, User user, Set<String> audiences) {
        log.debug("Logging out...");
        clearAccessTokens(user, audiences);
        clearOnBehalfOfCookie(response);
        log.info("Logged out");
    }

    private void clearAccessTokens(User user, Set<String> audiences) {
        try {
            audiences.forEach(audience -> egressAccessTokenFacade.clearAccessToken(user.id(), user.type(), audience));
        } catch (RuntimeException e) {
            log.error("FAILED to clear access tokens: {}", e.getMessage(), e);
        }
    }

    private void clearOnBehalfOfCookie(HttpServletResponse response) {
        try {
            cookieSetter.unsetCookie(response, CookieType.ON_BEHALF_OF_PID);
        } catch (RuntimeException e) {
            log.error("FAILED to clear on-behalf-of cookie: {}", e.getMessage(), e);
        }
    }
/*
    private void invalidate(HttpSession session) {
        try {
            session.invalidate();
        } catch (RuntimeException e) {
            log.error("FAILED to invalidate session: {}", e.getMessage(), e);
        }
    }*/
}
