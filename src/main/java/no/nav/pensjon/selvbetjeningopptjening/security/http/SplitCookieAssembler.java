package no.nav.pensjon.selvbetjeningopptjening.security.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class SplitCookieAssembler {

    public static String getCookieValue(HttpServletRequest request, CookieType cookieType) {
        String[] parts = getCookieValueParts(request.getCookies(), cookieType.getName());

        if (parts.length < 1) {
            return "";
        }

        return parts.length > 1
                ? parts[0] + parts[1]
                : parts[0];
    }

    private static String[] getCookieValueParts(Cookie[] cookies, String cookieNameBase) {
        if (cookies == null || cookies.length < 1) {
            return new String[0];
        }

        var twoParts = new String[2];
        int foundCount = 0;

        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();

            if (cookieName.equals(cookieNameBase)) {
                return new String[]{cookie.getValue()};
            }

            if (cookieName.equals(cookieNameBase + "1")) {
                twoParts[0] = cookie.getValue();

                if (++foundCount > 1) {
                    return twoParts;
                }
            } else if (cookieName.equals(cookieNameBase + "2")) {
                twoParts[1] = cookie.getValue();

                if (++foundCount > 1) {
                    return twoParts;
                }
            }
        }

        return new String[0];
    }
}
