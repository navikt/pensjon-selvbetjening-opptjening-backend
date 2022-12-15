package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SplitCookieAssemblerTest {

    private static final CookieType COOKIE_TYPE = CookieType.INTERNAL_USER_ACCESS_TOKEN; //.INTERNAL_USER_ID_TOKEN;
    private static final String COOKIE_NAME_BASE = COOKIE_TYPE.getName();

    @Mock
    HttpServletRequest request;

    @Test
    void when_nullCookies_then_getCookieValue_returns_emptyString() {
        when(request.getCookies()).thenReturn(null);
        String cookieValue = SplitCookieAssembler.getCookieValue(request, COOKIE_TYPE);
        assertEquals("", cookieValue);
    }

    @Test
    void when_noCookies_then_getCookieValue_returns_emptyString() {
        when(request.getCookies()).thenReturn(new Cookie[]{});
        String cookieValue = SplitCookieAssembler.getCookieValue(request, COOKIE_TYPE);
        assertEquals("", cookieValue);
    }

    @Test
    void when_noMatchingCookies_then_getCookieValue_returns_emptyString() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("no-match", "ignored")});
        String cookieValue = SplitCookieAssembler.getCookieValue(request, COOKIE_TYPE);
        assertEquals("", cookieValue);
    }

    @Test
    void when_matchingUnsplitCookie_then_getCookieValue_returns_cookieValue() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(COOKIE_NAME_BASE, "value")});
        String cookieValue = SplitCookieAssembler.getCookieValue(request, COOKIE_TYPE);
        assertEquals("value", cookieValue);
    }

    @Test
    void when_matchingSplitCookie_then_getCookieValue_returns_aggregateCookieValue() {
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie(COOKIE_NAME_BASE + "1", "part1"),
                new Cookie(COOKIE_NAME_BASE + "2", "part2")
        });

        String cookieValue = SplitCookieAssembler.getCookieValue(request, COOKIE_TYPE);

        assertEquals("part1part2", cookieValue);
    }

    @Test
    void when_matchingSplitCookieInReverseOrder_plusOtherCookies_then_getCookieValue_returns_aggregateTwoPartCookieValue() {
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie(COOKIE_NAME_BASE + "3", "ignoredPart3"),
                new Cookie("no-match1", "ignored"),
                new Cookie(COOKIE_NAME_BASE + "2", "part2"),
                new Cookie("no-match2", "ignored"),
                new Cookie(COOKIE_NAME_BASE + "1", "part1"),
                new Cookie("no-match3", "ignored")
        });

        String cookieValue = SplitCookieAssembler.getCookieValue(request, COOKIE_TYPE);

        assertEquals("part1part2", cookieValue);
    }
}
