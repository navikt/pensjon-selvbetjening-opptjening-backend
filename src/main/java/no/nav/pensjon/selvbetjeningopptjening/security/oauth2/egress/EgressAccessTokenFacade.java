package no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

/**
 * Orchestrates the classes used to obtain access tokens for calling other apps (i.e. egress calls).
 */
@Component
public class EgressAccessTokenFacade {

    private static final Logger log = LoggerFactory.getLogger(EgressAccessTokenFacade.class);
    private final AccessTokenGetter externalUserAccessTokenGetter;
    private final AccessTokenGetter internalUserAccessTokenGetter;
    private final AccessTokenGetter clientCredentialsAccessTokenGetter;
    private final AccessTokenGetter selfTestAccessTokenGetter;

    public EgressAccessTokenFacade(@Qualifier("external-user") AccessTokenGetter externalUserAccessTokenGetter,
                                   @Qualifier("internal-user") AccessTokenGetter internalUserAccessTokenGetter,
                                   @Qualifier("application") AccessTokenGetter clientCredentialsAccessTokenGetter) {
                                   //@Qualifier("self-test") AccessTokenGetter selfTestAccessTokenGetter) {
        this.externalUserAccessTokenGetter = externalUserAccessTokenGetter;
        this.internalUserAccessTokenGetter = internalUserAccessTokenGetter;
        this.clientCredentialsAccessTokenGetter = clientCredentialsAccessTokenGetter;
        this.selfTestAccessTokenGetter = null;// selfTestAccessTokenGetter;
    }

    public RawJwt getAccessToken(String ingressToken, String userId, UserType userType, String audience) {
        log.debug("Getting access token for {} user", userType.name());
        return getAccessTokenGetter(userType).getAccessToken(ingressToken, audience, userId);
    }

    public RawJwt getAccessToken(UserType userType, String audience) {
        log.debug("Getting access token for {} (access as application)", audience);
        return getAccessTokenGetter(userType).getAccessToken("", audience, "");
    }

    public void clearAccessToken(String userId, UserType userType, String audience) {
        if (!hasText(userId)) {
            return;
        }

        log.debug("Clearing access token for {} user", userType.name());
        getAccessTokenGetter(userType).clearAccessToken(audience, userId);
    }

    private AccessTokenGetter getAccessTokenGetter(UserType userType) {
        return switch (userType) {
            case EXTERNAL -> externalUserAccessTokenGetter;
            case INTERNAL -> internalUserAccessTokenGetter;
            case APPLICATION -> clientCredentialsAccessTokenGetter;
            case SELF_TEST -> selfTestAccessTokenGetter;
            default -> unsupported(userType);
        };
    }

    private static <T> T unsupported(UserType userType) {
        throw new IllegalArgumentException("Unsupported user type: " + userType);
    }
}
