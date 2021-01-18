package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.CryptoException;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieSetter;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.AuthorizationCodeFlow;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenRefresher;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow
 */
@RestController
@RequestMapping("oauth2/internal")
@Unprotected
public class InternalUserAuthorizationCodeFlow extends AuthorizationCodeFlow {

    private static final String SCOPE_URI = "https://graph.microsoft.com/user.read";
    private static final String OAUTH_2_SCOPE = "openid+profile+offline_access+" + encodedScopeUri();
    private static final String DEFAULT_AFTER_CALLBACK_REDIRECT_URI = "/api/opptjening";
    private final GroupChecker groupChecker;

    public InternalUserAuthorizationCodeFlow(@Qualifier("internal-user") OidcConfigGetter oidcConfigGetter,
                                             @Qualifier("internal-user") TokenGetter tokenGetter,
                                             @Qualifier("internal-user") TokenRefresher tokenRefresher,
                                             @Qualifier("internal-user") JwsValidator jwsValidator,
                                             GroupChecker groupChecker,
                                             CookieSetter cookieSetter,
                                             Crypto crypto,
                                             @Value("${internal-user.openid.client-id}") String clientId,
                                             @Value("${internal-user.openid.redirect-uri}") String callbackUri) {
        super(oidcConfigGetter,
                tokenGetter,
                tokenRefresher,
                jwsValidator,
                cookieSetter,
                crypto,
                clientId,
                callbackUri);
        this.groupChecker = requireNonNull(groupChecker);
    }

    @GetMapping("login")
    @Override
    public void login(HttpServletResponse response,
                      @RequestParam(value = "redirect", required = false) String redirectUri) throws IOException, CryptoException {
        super.login(response, redirectUri);
    }

    @PostMapping("callback")
    @Override
    public void callback(HttpServletResponse response,
                         @RequestParam(value = "code") String code,
                         @RequestParam(value = "state", required = false) String state) throws IOException {
        super.callback(response, code, state);
    }

    @GetMapping("refresh-token")
    @Override
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam(value = "redirect", required = false) String redirectUri) throws IOException {
        super.refreshToken(request, response, redirectUri);
    }

    @Override
    protected boolean isUserAuthorized(String accessToken) {
        return groupChecker.isUserAuthorized(accessToken);
    }

    @Override
    protected String getRefreshableToken(TokenData tokenData) {
        return tokenData.getIdToken();
    }

    @Override
    protected String oauth2Scope() {
        return OAUTH_2_SCOPE;
    }

    @Override
    protected CookieType accessTokenCookieType() {
        return CookieType.INTERNAL_USER_ACCESS_TOKEN;
    }

    @Override
    protected CookieType idTokenCookieType() {
        return CookieType.INTERNAL_USER_ID_TOKEN;
    }

    @Override
    protected String defaultAfterCallbackRedirectUri() {
        return DEFAULT_AFTER_CALLBACK_REDIRECT_URI;
    }

    private static String encodedScopeUri() {
        return URLEncoder.encode(SCOPE_URI, StandardCharsets.UTF_8);
    }
}
