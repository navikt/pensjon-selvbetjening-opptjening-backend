package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import io.jsonwebtoken.SigningKeyResolver;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Validator of JSON Web Signature (JWS) strings in the context of internal users.
 */
@Component
@Qualifier("internal-user")
public class InternalUserJwsValidator extends JwsValidator {

    public InternalUserJwsValidator(@Qualifier("internal-user") OidcConfigGetter oidcConfigGetter,
                                    @Qualifier("internal-user") SigningKeyResolver signingKeyResolver,
                                    @Value("${no.nav.security.jwt.issuer.aad.accepted_audience}") String acceptedAudience) {
        super(oidcConfigGetter, signingKeyResolver, acceptedAudience);
    }
}
