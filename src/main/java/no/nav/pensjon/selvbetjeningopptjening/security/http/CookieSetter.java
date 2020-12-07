package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieSetter {

    private static final int MAX_COOKIE_LENGTH = 3000;
    private final Boolean insecure;

    public CookieSetter(@Value("#{new Boolean('${cookies.insecure}')}") Boolean insecure) {
        this.insecure = insecure;
    }

    public void setCookie(HttpServletResponse response, CookieType type, String value) {
        if (value.length() > MAX_COOKIE_LENGTH) {
            response.addCookie(newCookie(type, value.substring(0, MAX_COOKIE_LENGTH), 1));
            response.addCookie(newCookie(type, value.substring(MAX_COOKIE_LENGTH), 2));
            return;
        }

        response.addCookie(newCookie(type, value, 0));
    }

    private Cookie newCookie(CookieType type, String value, int partNumber) {
        var cookie = new Cookie(type.getName() + (partNumber > 0 ? "" + partNumber : ""), value);
        cookie.setPath(type.getPath());
        cookie.setSecure(!insecure && type.isSecure());
        cookie.setHttpOnly(!insecure && type.isHttpOnly());
        return cookie;
    }
}
