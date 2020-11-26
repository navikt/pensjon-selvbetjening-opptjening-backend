package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieSetter {

    private final Boolean insecure;

    public CookieSetter(@Value("#{new Boolean('${cookies.insecure}')}") Boolean insecure) {
        this.insecure = insecure;
    }

    public void setCookie(HttpServletResponse response, CookieType type, String value) {
        response.addCookie(newCookie(type, value));
    }

    private Cookie newCookie(CookieType type, String value) {
        var cookie = new Cookie(type.getName(), value);
        cookie.setPath(type.getPath());
        cookie.setSecure(!insecure && type.isSecure());
        cookie.setHttpOnly(!insecure && type.isHttpOnly());
        return cookie;
    }
}
