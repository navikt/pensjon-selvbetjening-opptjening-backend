package no.nav.pensjon.selvbetjeningopptjening.security.token;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenRefresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TokenRefresherFacade {

    private static final Logger log = LoggerFactory.getLogger(TokenRefresherFacade.class);
    private final TokenRefresher externalUserTokenRefresher;
    private final TokenRefresher internalUserTokenRefresher;

    public TokenRefresherFacade(TokenRefresher externalUserTokenRefresher,
                                TokenRefresher internalUserTokenRefresher) {
        this.externalUserTokenRefresher = externalUserTokenRefresher;
        this.internalUserTokenRefresher = internalUserTokenRefresher;
    }

    public TokenData refreshToken(UserType userType, HttpServletRequest request) {
    //public TokenData refreshToken(UserType userType, String tokenId) {
        //log.debug("Refreshing token ID {} for {} user", tokenId, userType.name());
        log.debug("Refreshing token for {} user", userType.name());
        return getTokenRefresher(userType).refreshToken(request);
    }

    private TokenRefresher getTokenRefresher(UserType userType) {
        return switch (userType) {
            case EXTERNAL -> externalUserTokenRefresher;
            case INTERNAL -> internalUserTokenRefresher;
            default /* incl. SELF_TEST */ -> unsupported(userType);
        };
    }

    private static <T> T unsupported(UserType userType) {
        throw new IllegalArgumentException("Unsupported user type: " + userType);
    }
}
