package no.nav.pensjon.selvbetjeningopptjening.security.jwt;

import io.jsonwebtoken.*;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 * Validator of JSON Web Signature (JWS) strings.
 */
public class JwsValidator {

    private static final Logger log = LoggerFactory.getLogger(JwsValidator.class);
    private final OidcConfigGetter oidcConfigGetter;
    private final SigningKeyResolver signingKeyResolver;
    private final String acceptedAudience;

    protected JwsValidator(OidcConfigGetter oidcConfigGetter,
                           SigningKeyResolver signingKeyResolver,
                           String acceptedAudience) {
        this.oidcConfigGetter = requireNonNull(oidcConfigGetter, "oidcConfigGetter");
        this.signingKeyResolver = requireNonNull(signingKeyResolver, "signingKeyResolver");
        this.acceptedAudience = requireNonNull(acceptedAudience, "acceptedAudience");
    }

    public Jws<Claims> validate(String jwsString) {
        log.debug("Validating '{}'", jwsString);

        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver)
                .build()
                .parseClaimsJws(jwsString);

        validate(jws.getBody());
        return jws;
    }

    private void validate(Claims claims) {
        String audience = claims.getAudience();
        String issuer = claims.getIssuer();

        if (!acceptedAudience.equals(audience)) {
            String message = String.format("Invalid audience '%s'", audience);
            log.error(message);
            throw new JwtException(message);
        }

        if (!oidcConfigGetter.getIssuer().equals(issuer)) {
            String message = String.format("Invalid issuer '%s'", issuer);
            log.error(message);
            throw new JwtException(message);
        }
    }
}
