package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfo;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static java.util.Objects.requireNonNull;

public class TokenLoginInfoExtractor implements LoginInfoGetter {

    private static final String TOKEN_ISSUER = "selvbetjening";
    private static final String ACR_CLAIM_KEY = "acr"; // ACR = Authentication context class reference
    private static final String PID_CLAIM_KEY = "pid"; // PID = Personal identification number
    private static final String SUB_CLAIM_KEY = "sub"; // PID = Personal identification number

    private final TokenValidationContextHolder contextHolder;

    public TokenLoginInfoExtractor(TokenValidationContextHolder contextHolder) {
        this.contextHolder = requireNonNull(contextHolder);
    }

    public LoginInfo getLoginInfo() {
        if (getRequestAttributes() == null) {
            throw new JwtTokenUnauthorizedException();
        }

        JwtToken token = contextHolder.getTokenValidationContext().getJwtToken(TOKEN_ISSUER);
        return new LoginInfo(getPid(token), getSecurityLevel(token));
    }

    protected RequestAttributes getRequestAttributes() {
        return RequestContextHolder.getRequestAttributes();
    }

    private Pid getPid(JwtToken token) {
        JwtTokenClaims jwtTokenClaims = token.getJwtTokenClaims();
        String fnr;
        if(jwtTokenClaims.getStringClaim(PID_CLAIM_KEY) != null) {
            fnr = jwtTokenClaims.getStringClaim(PID_CLAIM_KEY);
        } else if (jwtTokenClaims.getStringClaim(SUB_CLAIM_KEY) != null) {
            fnr = jwtTokenClaims.getStringClaim(SUB_CLAIM_KEY);
        } else {
            throw new RuntimeException("No identifier found");
        }

        return new Pid(fnr, true);
    }

    private static LoginSecurityLevel getSecurityLevel(JwtToken token) {
        JwtTokenClaims claims = token.getJwtTokenClaims();

        return claims == null
                ? LoginSecurityLevel.NONE
                : getSecurityLevel(claims);
    }

    private static LoginSecurityLevel getSecurityLevel(JwtTokenClaims claims) {
        String authenticationContextClassReference = claims.getStringClaim(ACR_CLAIM_KEY);
        return LoginSecurityLevel.findByAcrValue(authenticationContextClassReference);
    }
}
