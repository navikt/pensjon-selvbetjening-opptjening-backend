package no.nav.pensjon.selvbetjeningopptjening.security.jwt;

import io.jsonwebtoken.*;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.MultiIssuerSupport;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2Handler;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * Validator of JSON Web Signature (JWS) strings.
 */
@Component
public class JwsValidator {

    private static final Logger log = LoggerFactory.getLogger(JwsValidator.class);
    private final MultiIssuerSupport multiIssuerSupport;
    private final SigningKeyResolver signingKeyResolver;
    private final boolean acceptMissingAudience;

    protected JwsValidator(MultiIssuerSupport multiIssuerSupport,
                           SigningKeyResolver signingKeyResolver,
                           @Value("${security.accept.missing-audience}") String acceptMissingAudience) {
        this.multiIssuerSupport = requireNonNull(multiIssuerSupport);
        this.signingKeyResolver = requireNonNull(signingKeyResolver);
        this.acceptMissingAudience = "true".equalsIgnoreCase(acceptMissingAudience);
    }

    public TokenInfo validate(String jwt) {
        log.trace("Validating '{}'", jwt);

        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKeyResolver(signingKeyResolver)
                    .build()
                    .parseClaimsJws(jwt);

            return validate(jwt, jws.getBody());
        } catch (ExpiredJwtException e) {
            return handleExpired(jwt, e);
        } catch (JwtException e) {
            log.warn("Bad JWT '{}'", jwt, e);
            return TokenInfo.invalid(jwt);
        }
    }

    private TokenInfo validate(String jwt, Claims claims) {
        Oauth2Handler oauth2Handler = multiIssuerSupport.getOauth2HandlerForIssuer(claims.getIssuer());
        String acceptedAudience = oauth2Handler.getAcceptedAudience();
        String audience = (String) claims.get(oauth2Handler.getAudienceClaimKey());
        String[] audiences = {};
        Object aud = claims.get(oauth2Handler.getAudienceClaimKey());
        if (aud instanceof String aud1) {
            audiences = new String[]{aud1};
            log.info("Audience: " +aud1);
        }
        else if (aud instanceof ArrayList<?>){
            ArrayList<String> audienceArrayList = (ArrayList<String>) aud;
            audiences = audienceArrayList.toArray(new String[0]);
            log.info("Audiences: " + Arrays.toString(audiences));
        }
        else {
            log.error("Not able to interpret audience type");
        }

        String userId = (String) claims.get(oauth2Handler.getUserIdClaimKey());

        if (audienceIsAccepted(acceptedAudience, audiences)) {
            return TokenInfo.valid(jwt, oauth2Handler.getUserType(), claims, userId);
        }

        log.warn("Audience mismatch; expected: '{}', actual: '{}'", acceptedAudience, audience);
        return TokenInfo.invalid(jwt, oauth2Handler.getUserType(), claims, userId);
    }

    private boolean audienceIsAccepted(String acceptedAudience, String[] audiences) {
        return (Arrays.asList(audiences).contains(acceptedAudience) || (audiences.length == 0 && acceptMissingAudience));
    }
    private TokenInfo handleExpired(String jwt, ExpiredJwtException e) {
        log.debug("Expired JWT: {}", e.getMessage());
        Claims claims = e.getClaims();
        Oauth2Handler oauth2Handler = multiIssuerSupport.getOauth2HandlerForIssuer(claims.getIssuer());
        String userId = (String) claims.get(oauth2Handler.getUserIdClaimKey());
        return TokenInfo.invalid(jwt, oauth2Handler.getUserType(), claims, userId);
    }
}
