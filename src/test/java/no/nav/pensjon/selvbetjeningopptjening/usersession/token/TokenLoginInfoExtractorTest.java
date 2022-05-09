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

    private static final String SYNTHETIC_FNR = "29885596930";

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
        return "eyJraWQiOiJ2UHBaZW9HOGRkTHpmdHMxLWxnc3VnOHNyYVd3bW04dHhJaGJ3Y1h3R01JIiwiYWxnIjoiUlMyNTYifQ" +
                ".eyJzdWIiOiJUUmFiMWJ2VkdWYk1GZHUyM1BaQU1zWVd1Q3BHRnB2b2pFSmJleFZkNEdjPSIsImlzcyI6Imh0dHBzOlwvXC9vaWRjLXZlcjIuZGlmaS5ub1wvaWRwb3J0ZW4tb2lkYy1wcm92aWRlclwvIiwiY2xpZW50X2FtciI6ImNsaWVudF9zZWNyZXRfcG9zdCIsInBpZCI6IjI5ODg1NTk2OTMwIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImNsaWVudF9pZCI6ImQ4Mjk3MjQwLTIzYzgtNDBjOC1iMWM4LWEyOTNhZjczNjk3MiIsImF1ZCI6Imh0dHBzOlwvXC9uYXYubm8iLCJhY3IiOiJMZXZlbDQiLCJzY29wZSI6Im9wZW5pZCIsImV4cCI6MTY1MjA4Njc4NywiaWF0IjoxNjUyMDgzMTg3LCJjbGllbnRfb3Jnbm8iOiI4ODk2NDA3ODIiLCJqdGkiOiJhZ3pLd2xjYnhHWnU2T054OS1RRl9pSlY2cUtrTURJOV92dk5hLWtqSDFRIiwiY29uc3VtZXIiOnsiYXV0aG9yaXR5IjoiaXNvNjUyMy1hY3RvcmlkLXVwaXMiLCJJRCI6IjAxOTI6ODg5NjQwNzgyIn19" +
                ".VJUpyZmzLZonuldaSRqMNrc-TI1MwFspHntrkwLJhSLjX4g-BnRMmO8y8b1sxHxmNxR3D-FeUCgMB9q8wlntMyVJV2h649yDRFnFoNwahoUYv7a4MP4W0Bqf5vxGt8B3c5kmFmdq5bGNZXZa_A1m6_ewR1dG5JpkAdaDLnyo9vDehyClj-kjSdtOg2QZbngTpKeUrma1ve0AsitZ-f1oC0VlMRj77ItkW9DM6uZvPhCJZjslS-r_-t3_ICUGRh4sGgP6TebIZr3-0yu_9P9FrV2F9AAm8CinEeGoROhaBnm9hgbS8MekhY4ytlNWn_zWADJPoph3eXvbtNaHkJ1jOw";
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
