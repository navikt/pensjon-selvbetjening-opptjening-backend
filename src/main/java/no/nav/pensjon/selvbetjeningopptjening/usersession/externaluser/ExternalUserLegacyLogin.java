package no.nav.pensjon.selvbetjeningopptjening.usersession.externaluser;

import no.nav.pensjon.selvbetjeningopptjening.usersession.LegacyLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Logs the user in via Loginservice (which is a legacy service in NAV).
 */
@RestController
@RequestMapping("oauth2")
public class ExternalUserLegacyLogin {

    private static final Logger log = LoggerFactory.getLogger(ExternalUserLegacyLogin.class);
    private final LegacyLogin login;

    public ExternalUserLegacyLogin(@Qualifier("external-user") LegacyLogin login) {
        this.login = login;
    }

    @GetMapping("legacylogin")
    public void login(HttpServletResponse response,
                      @RequestParam(value = "redirect", required = false) String redirectUri) throws IOException {
        log.info("Received legacy login request with redirect to '{}'", redirectUri);
        String uri = login.getUrl() + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        log.debug("Redirecting to '{}'", uri);
        response.sendRedirect(uri);
    }
}
