package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfo;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TokenLoginInfoExtractorTest {

    private static final String SYNTHETIC_FNR = "12117121168";

    @Mock
    TokenValidationContextHolder tokenValidationContextHolder;
    @Mock
    RequestAttributes requestAttributes;

    @Test
    void getLoginInfo_shall_extract_loginInfo_from_tokenValidationContext() {
        when(tokenValidationContextHolder.getTokenValidationContext())
                .thenReturn(contextWithSecurityLevel4AndSyntheticFnr());

        LoginInfo loginInfo = new TestTokenLoginInfoExtractor(tokenValidationContextHolder).getLoginInfo();

        assertEquals(SYNTHETIC_FNR, loginInfo.getPid().getPid());
        assertEquals(LoginSecurityLevel.LEVEL4, loginInfo.getSecurityLevel());
    }

    private static TokenValidationContext contextWithSecurityLevel4AndSyntheticFnr() {
        Map<String, JwtToken> tokenMap = new HashMap<>();
        tokenMap.put("selvbetjening", new JwtToken(jwtWithSecurityLevel4AndSyntheticFnr()));
        return new TokenValidationContext(tokenMap);
    }

    private static String jwtWithSecurityLevel4AndSyntheticFnr() {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6ImZ5akpfczQwN1ZqdnRzT0NZcEItRy1IUTZpYzJUeDNmXy1JT3ZqVEFqLXcifQ" +
                ".eyJleHAiOjE2MDc0MzQ0MTcsIm5iZiI6MTYwNzQzMDgxNywidmVyIjoiMS4wIiwiaXNzIjoiaHR0cHM6Ly9uYXZ0ZXN0YjJjLmIyY2xvZ2luLmNvbS9kMzhmMjVhYS1lYWI4LTRjNTAtOWYyOC1lYmY5MmMxMjU2ZjIvdjIuMC8iLCJzdWIiOiIxMjExNzEyMTE2OCIsImF1ZCI6IjAwOTBiNmUxLWZmY2MtNGMzNy1iYzIxLTA0OWY3ZDFmMGZlNSIsImFjciI6IkxldmVsNCIsIm5vbmNlIjoibDhuTVAtejhtd2ZLRmZvWHpkWUtmQ0M2RmlZTnpyazFmTlNwUFVRdlpUVSIsImlhdCI6MTYwNzQzMDgxNywiYXV0aF90aW1lIjoxNjA3NDMwODE2LCJqdGkiOiJFc3Blbi5Pc2thci5Ham9zdG9sQG5hdi5ubzpjYzRjMmFkMy1iY2UwLTRhMDEtYTA3OC0xNGJmNTljZDhiMDEiLCJhdF9oYXNoIjoiOTVNclZfSGtXWGR1QjVnMU9MZW5OdyJ9" +
                ".ckCi8u1cY6Gd75lZQw22dnM_OOVbwZzv5KljIRwNP5nIyCEVelyzl_QqUom8ttQouaCGgaX_02RQdSfB_EI1uZVI633oqJtplOyJ1Il6oL7BZH03b85m7GUqHOTaM4zoV5jV-RLO33L0NhfmrKeMsUVfuXATTb1AWw2wVPiQ1eDBJQhzk-jiHA8oOGRGsOVuJ0NhBFvaGT_ZDXXjYWtVWSZ37rIUoMsppoUwfMH-DDk7tQMeYSx2Bl4C2UA_C4t0SBq-jwVu6qkEmisBjoYasToNa2nnJRDKXvSbafGjjlmwSv25DOPyLz23HfutHGsD5hzkgRst-5f7GMuy_9NP5w";
    }

    private class TestTokenLoginInfoExtractor extends TokenLoginInfoExtractor {

        TestTokenLoginInfoExtractor(TokenValidationContextHolder contextHolder) {
            super(contextHolder);
        }

        @Override
        protected RequestAttributes getRequestAttributes() {
            return requestAttributes;
        }
    }
}
