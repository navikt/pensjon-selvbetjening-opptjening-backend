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
    private final TokenValidationContextHolder contextHolder;

    public TokenLoginInfoExtractor(TokenValidationContextHolder contextHolder) {
        this.contextHolder = requireNonNull(contextHolder);
    }

    public LoginInfo getLoginInfo() {
        if (getRequestAttributes() == null) {
            throw new JwtTokenUnauthorizedException("Token not found (no request attributes)");
        }

        JwtToken token = contextHolder.getTokenValidationContext().getJwtToken(TOKEN_ISSUER);
        return new LoginInfo(getPid(token), getSecurityLevel(token));
    }

    protected RequestAttributes getRequestAttributes() {
        return RequestContextHolder.getRequestAttributes();
    }

    private static Pid getPid(JwtToken token) {
        return new Pid(token.getSubject(), true);
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
