package no.nav.pensjon.selvbetjeningopptjening.mock;

import com.nimbusds.jwt.SignedJWT;
import no.nav.pensjon.selvbetjeningopptjening.LocalOpptjeningApplication;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.springframework.util.ObjectUtils.isEmpty;

@RestController
@RequestMapping("api")
public class MockLoginEndpoint {

    private static final String AUDIENCE = "local-opptjening";
    private static final String COOKIE_NAME = "mock-idtoken";
    private static final String ISSUER_ID = "default";
    private static final long EXPIRY = 1000000L; // approx. 12 days in seconds

    @GetMapping("/mocklogin/{pid}")
    public void login(HttpServletResponse response,
                      @RequestParam(value = "redirect", required = false) String redirectUri,
                      @PathVariable("pid") String pid) throws IOException {
        if (pid == null) {
            response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            return;
        }

        response.addCookie(authCookie(pid));

        if (isEmpty(redirectUri)) {
            try (Writer writer = response.getWriter()) {
                writer.write("Logged in OK");
            }

            return;
        }

        response.sendRedirect(redirectUri);
    }

    private Cookie authCookie(String pid) {
        var cookie = new Cookie(COOKIE_NAME, initializeAuth(pid).serialize());
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        return cookie;
    }

    private SignedJWT initializeAuth(String pid) {
        var callback = new DefaultOAuth2TokenCallback(ISSUER_ID, pid, "JWT", singletonList(AUDIENCE), claims(pid), EXPIRY);
        MockOAuth2Server authServer = LocalOpptjeningApplication.getAuthServer();
        authServer.enqueueCallback(callback);
        return authServer.issueToken(ISSUER_ID, "dummy", callback);
    }

    private Map<String, String> claims(String pid) {
        Map<String, String> claims = new HashMap<>();
        claims.put("acr", "Level4");
        claims.put("pid", pid);
        return claims;
    }
}
