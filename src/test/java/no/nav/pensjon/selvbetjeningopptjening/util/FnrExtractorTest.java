package no.nav.pensjon.selvbetjeningopptjening.util;

import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
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
class FnrExtractorTest {

    @Mock
    TokenValidationContextHolder contextHolder;
    @Mock
    RequestAttributes requestAttributes;

    @Test
    void extract_extracts_token_when_present_and_valid() {
        when(contextHolder.getTokenValidationContext()).thenReturn(tokenValidationContext());
        String fnr = new TestFnrExtractor(contextHolder).extract();
        assertEquals("srvpensjon", fnr);
    }

    @Test
    void extract_throws_JwtTokenUnauthorizedException_when_no_token() {
        JwtTokenUnauthorizedException e = assertThrows(JwtTokenUnauthorizedException.class, () -> new FnrExtractor(contextHolder).extract());
        assertEquals("Token not found (no request attributes)", e.getMessage());
    }

    private static TokenValidationContext tokenValidationContext() {
        Map<String, JwtToken> tokenMap = new HashMap<>();
        tokenMap.put("selvbetjening", new JwtToken(jwt()));
        return new TokenValidationContext(tokenMap);
    }

    private static String jwt() {
        return "eyJraWQiOiJkZWZjZTlkYi05NTk0LTRhOTUtYThjOS1lNTZiZmY2ZDlmYmMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9" +
                ".eyJzdWIiOiJzcnZwZW5zam9uIiwiYXVkIjpbInNydnBlbnNqb24iLCJwcmVwcm9kLmxvY2FsIl0sInZlciI6IjEuMCIsIm5iZiI6MTYwMDk0NTAxNiwiYXpwIjoic3J2cGVuc2pvbiIsImlkZW50VHlwZSI6IlN5c3RlbXJlc3N1cnMiLCJhdXRoX3RpbWUiOjE2MDA5NDUwMTYsImlzcyI6Imh0dHBzOlwvXC9zZWN1cml0eS10b2tlbi1zZXJ2aWNlLm5haXMucHJlcHJvZC5sb2NhbCIsImV4cCI6MTYwMDk0ODYxNiwiaWF0IjoxNjAwOTQ1MDE2LCJqdGkiOiI2MDU3Mzc3MS04MTZhLTQxNjYtOWQ2Yi1hYTYxMGFmZDIwNmYifQ" +
                ".dFjzDGxwPbNvHVykflTC5v6Kl44LljoXMjPyxvKY5I6fGNVRUU6N9nzjVycog0IqPcNESeRjP3iw7Js7Rpns-hkaS5d1IhTqo0mWtr5fJ65mNkD9vf3tmGQYGVLmOl5MW8ySqSyiUeZXsD8JVm7inqKCfOpShwm8jNV2wikW3pfmI--vnsVdOTAtkFFCXMwzhLgsioFb9ajBBn5MYJ3Z87WH_2u40RGRc5vpvVf8Jc5KQXOY5LT_k_HgC1JklO4-AIfUsJlolx-KRlw_62nPLA1R3hZDfYkvBi-zU4yeVgLGH9IdlFK3sT2oIJTSUwiOLazTAOEL7MnX1gvDzz6anA";
    }

    private class TestFnrExtractor extends FnrExtractor {

        TestFnrExtractor(TokenValidationContextHolder contextHolder) {
            super(contextHolder);
        }

        @Override
        protected RequestAttributes getRequestAttributes() {
            return requestAttributes;
        }
    }
}