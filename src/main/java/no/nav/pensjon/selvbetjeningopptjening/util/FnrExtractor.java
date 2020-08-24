package no.nav.pensjon.selvbetjeningopptjening.util;

import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import org.springframework.web.context.request.RequestContextHolder;

import static java.util.Objects.requireNonNull;

public class FnrExtractor implements StringExtractor {

    private static final String ISSUER = "selvbetjening";
    private final TokenValidationContextHolder contextHolder;

    public FnrExtractor(TokenValidationContextHolder contextHolder) {
        this.contextHolder = requireNonNull(contextHolder);
    }

    @Override
    public String extract() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            throw new JwtTokenUnauthorizedException("Token not found (no request attributes)");
        }

        return getFnr();
    }

    private String getFnr() {
        return contextHolder.getTokenValidationContext().getJwtToken(ISSUER).getSubject();
    }
}
