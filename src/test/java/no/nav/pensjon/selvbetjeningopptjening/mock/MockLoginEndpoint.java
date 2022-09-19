package no.nav.pensjon.selvbetjeningopptjening.mock;

import com.nimbusds.jwt.SignedJWT;
import no.nav.pensjon.selvbetjeningopptjening.LocalOpptjeningApplication;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.springframework.util.ObjectUtils.isEmpty;

@RestController
@RequestMapping("api")
@Unprotected
public class MockLoginEndpoint {

    private static final String AUDIENCE = "local-opptjening";
    private static final String COOKIE_NAME = "mock-idtoken";
    private static final String ISSUER_ID = "default";
    private static final long EXPIRY = 1000000L; // approx. 12 days in seconds
    private String pid;

    @GetMapping("/mocklogin/{pid}")
    public void login(HttpServletResponse response,
                      @RequestParam(value = "redirect", required = false) String redirectUri,
                      @PathParam("pid") String pid) throws IOException {
        this.pid=pid;
        response.addCookie(authCookie());

        if (isEmpty(redirectUri)) {
            try (Writer writer = response.getWriter()) {
                writer.write("Logged in OK");
            }
            return;
        }

        response.sendRedirect(redirectUri);
    }

    private Cookie authCookie() {
        var cookie = new Cookie(COOKIE_NAME, initializeAuth().serialize());
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        return cookie;
    }

    private SignedJWT initializeAuth() {
        var callback = new DefaultOAuth2TokenCallback(ISSUER_ID, pid, "JWT", singletonList(AUDIENCE), claims(), EXPIRY);
        MockOAuth2Server authServer = LocalOpptjeningApplication.getAuthServer();
        authServer.enqueueCallback(callback);
        return authServer.issueToken(ISSUER_ID, "dummy", callback);
    }

    private Map<String, String> claims() {
        Map<String, String> claims = new HashMap<>();
        claims.put("acr", "Level4");
        claims.put("pid", pid);
        return claims;
    }
}
