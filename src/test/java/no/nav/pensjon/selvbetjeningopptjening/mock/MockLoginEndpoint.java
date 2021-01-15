package no.nav.pensjon.selvbetjeningopptjening.mock;

import com.nimbusds.jwt.SignedJWT;
import no.nav.pensjon.selvbetjeningopptjening.LocalOpptjeningApplication;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api")
@Unprotected
public class MockLoginEndpoint {

    private static final String AUDIENCE = "local-opptjening";
    private static final String COOKIE_NAME = "mock-idtoken";
    private static final String ISSUER_ID = "default";
    private static final long EXPIRY = 1000000L; // approx. 12 days in seconds
    private final String fnr;

    public MockLoginEndpoint(@Value("${fnr}") String fnr) {
        this.fnr = fnr;
    }

    @GetMapping("/mocklogin")
    public void login(HttpServletResponse response) {
        response.addCookie(authCookie());
    }

    private Cookie authCookie() {
        var cookie = new Cookie(COOKIE_NAME, initializeAuth().serialize());
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        return cookie;
    }

    private SignedJWT initializeAuth() {
        var callback = new DefaultOAuth2TokenCallback(ISSUER_ID, fnr, AUDIENCE, claims(), EXPIRY);
        MockOAuth2Server authServer = LocalOpptjeningApplication.getAuthServer();
        authServer.enqueueCallback(callback);
        return authServer.issueToken(ISSUER_ID, "dummy", callback);
    }

    private static Map<String, String> claims() {
        Map<String, String> claims = new HashMap<>();
        claims.put("acr", "Level4");
        return claims;
    }
}
