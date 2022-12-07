package no.nav.pensjon.selvbetjeningopptjening.usersession;

import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.CryptoException;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSpec;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2FlowException;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

public abstract class AuthorizationCodeFlow {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationCodeFlow.class);
    private final TokenGetter tokenGetter;
    private final TokenRefresher tokenRefresher;
    private final Oauth2ConfigGetter oauth2ConfigGetter;
    private final CookieSetter cookieSetter;
    private final Crypto crypto;
    private final String clientId;
    private final String callbackUri;
    private final LegacyLogin legacyLogin;

    protected AuthorizationCodeFlow(Oauth2ConfigGetter oauth2ConfigGetter,
                                    TokenGetter tokenGetter,
                                    TokenRefresher tokenRefresher,
                                    CookieSetter cookieSetter,
                                    Crypto crypto,
                                    String clientId,
                                    String callbackUri,
                                    LegacyLogin legacyLogin) {
        this.tokenGetter = requireNonNull(tokenGetter, "tokenGetter");
        this.oauth2ConfigGetter = requireNonNull(oauth2ConfigGetter, "oauth2ConfigGetter");
        this.tokenRefresher = requireNonNull(tokenRefresher, "tokenRefresher");
        this.cookieSetter = requireNonNull(cookieSetter, "cookieSetter");
        this.crypto = requireNonNull(crypto, "crypto");
        this.clientId = requireNonNull(clientId, "clientId");
        this.callbackUri = requireNonNull(callbackUri, "callbackUri");
        this.legacyLogin = requireNonNull(legacyLogin, "legacyLogin");
    }

    protected void login(HttpServletResponse response,
                         String redirectUri) throws IOException, CryptoException {
        log.debug("Login request received");

        String uri = new Oauth2ParamBuilder()
                .scope(oauth2Scope())
                .clientId(clientId)
                .callbackUri(callbackUri)
                .state(encryptedState(redirectUri))
                .buildAuthorizationUri(oauth2ConfigGetter.getAuthorizationEndpoint());

        redirect(response, uri);
    }

    protected void callback(HttpServletResponse response,
                            String code,
                            String state) throws IOException {
        if (!hasText(code)) {
            response.sendRedirect("/api/error");
            return;
        }

        log.info("Callback received. Code: '{}...'. State: '{}'.", code.substring(0, 3), state);

        try {
            String redirectUri = new StateValidator(crypto).extractRedirectUri(state);
            TokenData tokenData = tokenGetter.getTokenData(TokenAccessParam.authorizationCode(code), "");
            setCookies(response, tokenData);
            decodeAndRedirect(response, redirectUri);
        } catch (JwtException e) {
            log.warn(format("Bad JWT. Message: '%s'.", e.getMessage()));
            unauthorized(response);
        } catch (CryptoException | Oauth2FlowException e) {
            log.warn(format("Bad state. Message: '%s'.", e.getMessage()));
            unauthorized(response);
        }
    }

    protected void refreshToken(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam(value = "redirect", required = false) String redirectUri) throws IOException {
        log.info("Token refresh request received");

        try {
            TokenData tokenData = tokenRefresher.refreshToken(request);
            setCookies(response, tokenData);
            decodeAndRedirect(response, redirectUri);
        } catch (MissingTokenException e) {
            log.warn(format("Missing token. Message: '%s'.", e.getMessage()));
            unauthorized(response);
        } catch (JwtException e) {
            log.warn(format("Bad JWT. Message: '%s'.", e.getMessage()));
            unauthorized(response);
        }
    }

    protected abstract String oauth2Scope();

    protected abstract String defaultAfterCallbackRedirectUri();

    protected abstract CookieType accessTokenCookieType();

    private String encryptedState(String redirectUri) throws CryptoException {
        String uri = hasText(redirectUri) ? redirectUri : defaultAfterCallbackRedirectUri();
        String state = format("%s:%s", currentTimeMillis(), URLEncoder.encode(uri, StandardCharsets.UTF_8));
        return crypto.encrypt(state);
    }

    private void decodeAndRedirect(HttpServletResponse response, String encodedUri) throws IOException {
        String uri = decodeUri(encodedUri);

        if (legacyLogin.isEnabled()) {
            uri = legacyLogin.getUrl() + URLEncoder.encode(uri, StandardCharsets.UTF_8);
        }

        redirect(response, uri);
    }

    private void redirect(HttpServletResponse response, String uri) throws IOException {
        log.debug("Redirecting to '{}'", uri);
        response.sendRedirect(uri);
    }

    /**
     * When setting cookies be aware that total size of cookies cannot exceed max. header size in
     * the NGINX server used by NAIS in the cluster (will result in "502 Bad Gateway" error).
     * Also, individual cookies cannot exceed 4000 bytes.
     */
    private void setCookies(HttpServletResponse response, TokenData tokenData) {
        List<CookieSpec> cookieSpecs = new ArrayList<>();
        cookieSpecs.add(new CookieSpec(accessTokenCookieType(), tokenData.getAccessToken()));
        cookieSpecs.add(new CookieSpec(CookieType.REFRESH_TOKEN, tokenData.getRefreshToken()));

        if (log.isDebugEnabled()) {
            log.debug("Access token: {}", tokenData.getAccessToken());
            log.debug("Refresh token: {}", tokenData.getRefreshToken());
        }

        cookieSetter.setCookies(response, cookieSpecs);
    }

    private String decodeUri(String encodedUri) {
        return hasText(encodedUri)
                ? URLDecoder.decode(encodedUri, StandardCharsets.UTF_8)
                : defaultAfterCallbackRedirectUri();
    }

    private static void unauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
