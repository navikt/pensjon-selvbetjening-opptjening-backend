package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.lang.String.format;

@Component
public class CookieSetter {

    private static final int MAX_AGE = 7200; // 2 hours in seconds
    private static final String SAME_SITE = "Lax";
    private final String domain;
    private final Boolean insecure;

    public CookieSetter(@Value("${cookies.domain}") String domain,
                        @Value("#{new Boolean('${cookies.insecure}')}") Boolean insecure) {
        this.domain = domain;
        this.insecure = insecure;
    }

    public void setCookies(HttpServletResponse response, List<CookieSpec> cookieSpecs) {
        boolean firstHeader = true;

        for (CookieSpec spec : cookieSpecs) {
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, headerValue(spec, MAX_AGE));
                firstHeader = false;
            } else {
                response.addHeader(HttpHeaders.SET_COOKIE, headerValue(spec, MAX_AGE));
            }
        }
    }

    public void unsetCookie(HttpServletResponse response, CookieType type) {
        response.setHeader(
                HttpHeaders.SET_COOKIE,
                headerValue(new CookieSpec(type, ""), 0));
    }

    private String headerValue(CookieSpec spec, int maxAge) {
        CookieType cookieType = spec.getCookieType();

        return format("%s=%s; %s=%s; %s=%s; %s=%s; %s=%d%s; %s",
                cookieType.getName(), spec.getCookieValue(),
                CookieAttributes.DOMAIN, domain,
                CookieAttributes.PATH, cookieType.getPath(),
                CookieAttributes.SAME_SITE, SAME_SITE,
                CookieAttributes.MAX_AGE, maxAge,
                getSecureAttribute(cookieType),
                CookieAttributes.HTTP_ONLY);
    }

    private String getSecureAttribute(CookieType type) {
        return !insecure && type.isSecure() ? "; " + CookieAttributes.SECURE : "";
    }
}
