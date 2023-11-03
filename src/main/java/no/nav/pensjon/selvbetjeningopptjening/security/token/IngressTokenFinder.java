package no.nav.pensjon.selvbetjeningopptjening.security.token;

import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.QueryStringParser;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser.QueryParamNames;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.hasText;

@Component
public class IngressTokenFinder {

    private static final String AUTH_TYPE = "Bearer";
    private static final String LOGINSERVICE_ID_TOKEN_COOKIE_NAME = "selvbetjening-idtoken";

    private static final List<String> externalAuthCookies = List.of(
            LOGINSERVICE_ID_TOKEN_COOKIE_NAME,
            CookieType.EXTERNAL_USER_ACCESS_TOKEN.getName(),
            "mock-idtoken");

    private static final List<String> internalAuthCookies = List.of(CookieType.INTERNAL_USER_ACCESS_TOKEN.getName());
    private static final Logger log = LoggerFactory.getLogger(IngressTokenFinder.class);
    private final JwsValidator jwsValidator;
    private final TokenRefresherFacade tokenRefresher;

    public IngressTokenFinder(JwsValidator jwsValidator, TokenRefresherFacade tokenRefresher) {
        this.jwsValidator = jwsValidator;
        this.tokenRefresher = tokenRefresher;
    }

    public TokenInfo getIngressTokenInfo(HttpServletRequest request, boolean skipRefresh) {
        TokenInfo tokenInfo = getTokenInfo(request);

        if (tokenInfo.isValid()) {
            log.debug("Valid token");
            return tokenInfo;
        }

        if (!tokenInfo.hasUser()) {
            log.info(hasLength(tokenInfo.getJwt()) ? "Invalid token: No user ID" : "No JWT");
            return TokenInfo.invalid();
        }

        if (skipRefresh) {
            return TokenInfo.invalid();
        }

        log.debug("Refreshing token...");

        TokenData tokenData = tokenRefresher.refreshToken(
                tokenInfo.getUserType(),
                request);

        log.info("Token refreshed");
        return jwsValidator.validate(tokenData.getAccessToken());
    }

    private TokenInfo getTokenInfo(HttpServletRequest request) {
        TokenInfo tokenInfo = getTokenInfoFromHeader(request);
        return tokenInfo.isValid() ? tokenInfo : getTokenInfoFromCookies(request);
    }

    private TokenInfo getTokenInfoFromCookies(HttpServletRequest request) {
        log.debug("Looking for token in cookie...");
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return TokenInfo.invalid();
        }

        if (userIsInternal(request)) {
            return findInternalAuthTokenIn(cookies).orElse(TokenInfo.invalid());
        }

        return findExternalAuthTokenIn(cookies)
                .or(() -> findInternalAuthTokenIn(cookies))
                .orElseGet(TokenInfo::invalid);
    }

    private Optional<TokenInfo> findExternalAuthTokenIn(Cookie[] cookies) {
        return findTokenInCookies(cookies, externalAuthCookies);
    }

    private Optional<TokenInfo> findInternalAuthTokenIn(Cookie[] cookies) {
        return findTokenInCookies(cookies, internalAuthCookies);
    }

    private Optional<TokenInfo> findTokenInCookies(Cookie[] cookies, List<String> cookieNames) {
        return stream(cookies)
                .filter(cookie -> cookieNames.contains(cookie.getName()))
                .map(Cookie::getValue)
                .map(jwsValidator::validate)
                .filter(TokenInfo::isValid)
                .findFirst();
    }

    private TokenInfo getTokenInfoFromHeader(HttpServletRequest request) {
        log.debug("Looking for token in header...");
        String token = getTokenFromHeader(request);

        return token == null
                ? TokenInfo.invalid()
                : jwsValidator.validate(token);
    }

    private static boolean userIsInternal(HttpServletRequest request) {
        return hasText(getVirtualLoggedInPid(request));
    }

    private static String getVirtualLoggedInPid(HttpServletRequest request) {
        String pid = QueryStringParser.getValue(request.getQueryString(), QueryParamNames.PID);
        return hasText(pid) ? pid : "";
    }

    private static String getTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return authHeader != null && authHeader.startsWith(AUTH_TYPE + " ")
                ? authHeader.substring(AUTH_TYPE.length() + 1)
                : null;
    }
}
