package no.nav.pensjon.selvbetjeningopptjening.usersession;

import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.CryptoException;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2FlowException;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2ParamBuilder;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
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

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

public abstract class AuthorizationCodeFlow {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationCodeFlow.class);
    private final TokenGetter tokenGetter;
    private final TokenRefresher tokenRefresher;
    private final JwsValidator jwsValidator;
    private final OidcConfigGetter oidcConfigGetter;
    private final CookieSetter cookieSetter;
    private final Crypto crypto;
    private final String clientId;
    private final String callbackUri;

    protected AuthorizationCodeFlow(OidcConfigGetter oidcConfigGetter,
                                    TokenGetter tokenGetter,
                                    TokenRefresher tokenRefresher,
                                    JwsValidator jwsValidator,
                                    CookieSetter cookieSetter,
                                    Crypto crypto,
                                    String clientId,
                                    String callbackUri) {
        this.tokenGetter = requireNonNull(tokenGetter, "tokenGetter");
        this.oidcConfigGetter = requireNonNull(oidcConfigGetter, "oidcConfigGetter");
        this.tokenRefresher = requireNonNull(tokenRefresher, "tokenRefresher");
        this.jwsValidator = requireNonNull(jwsValidator, "jwsValidator");
        this.cookieSetter = requireNonNull(cookieSetter, "cookieSetter");
        this.crypto = requireNonNull(crypto, "crypto");
        this.clientId = requireNonNull(clientId, "clientId");
        this.callbackUri = requireNonNull(callbackUri, "callbackUri");
    }

    protected void login(HttpServletResponse response,
                         String redirectUri) throws IOException, CryptoException {
        log.debug("Login request received");

        String uri = new Oauth2ParamBuilder()
                .scope(oauth2Scope())
                .clientId(clientId)
                .callbackUri(callbackUri)
                .state(encryptedState(redirectUri))
                .buildAuthorizationUri(oidcConfigGetter.getAuthorizationEndpoint());

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
            TokenData tokenData = tokenGetter.getTokenData(TokenAccessParam.authorizationCode(code));
            jwsValidator.validate(tokenData.getIdToken());
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
            jwsValidator.validate(getRefreshableToken(tokenData));
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

    protected abstract String getRefreshableToken(TokenData tokenData);

    protected abstract CookieType accessTokenCookieType();

    protected abstract CookieType idTokenCookieType();

    private String encryptedState(String redirectUri) throws CryptoException {
        String uri = hasText(redirectUri) ? redirectUri : defaultAfterCallbackRedirectUri();
        String state = format("%s:%s", currentTimeMillis(), URLEncoder.encode(uri, StandardCharsets.UTF_8));
        return crypto.encrypt(state);
    }

    private void decodeAndRedirect(HttpServletResponse response, String encodedUri) throws IOException {
        String finalUri = decodeUri(encodedUri);
        redirect(response, finalUri);
    }

    private void redirect(HttpServletResponse response, String uri) throws IOException {
        log.debug("Redirecting");
        response.sendRedirect(uri);
    }

    private void setCookies(HttpServletResponse response, TokenData tokenData) {
        cookieSetter.setCookie(response, accessTokenCookieType(), tokenData.getAccessToken());
        cookieSetter.setCookie(response, CookieType.REFRESH_TOKEN, tokenData.getRefreshToken());

        if (tokenData.hasIdToken()) {
            cookieSetter.setCookie(response, idTokenCookieType(), tokenData.getIdToken());
        } else {
            log.info("No ID token cookie set");
        }
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
