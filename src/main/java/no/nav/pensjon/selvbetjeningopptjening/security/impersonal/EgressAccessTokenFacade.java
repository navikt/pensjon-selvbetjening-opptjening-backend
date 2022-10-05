package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the classes used to obtain access tokens for calling other apps (i.e. egress calls).
 */
@Service
public class EgressAccessTokenFacade {

    private static final Logger log = LoggerFactory.getLogger(EgressAccessTokenFacade.class);
    private final AccessTokenGetter clientCredentialsAccessTokenGetter;

    public EgressAccessTokenFacade(AccessTokenGetter clientCredentialsAccessTokenGetter) {
        this.clientCredentialsAccessTokenGetter = clientCredentialsAccessTokenGetter;
    }

    public RawJwt getAccessToken(String audience) {
        log.debug("Getting access token for {} (access as application)", audience);
        return clientCredentialsAccessTokenGetter.getAccessToken(audience);
    }

    public void clearAccessToken(String audience) {
        clientCredentialsAccessTokenGetter.clearAccessToken(audience);
    }
}
