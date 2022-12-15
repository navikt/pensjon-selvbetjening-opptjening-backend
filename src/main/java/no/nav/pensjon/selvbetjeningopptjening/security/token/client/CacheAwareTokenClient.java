package no.nav.pensjon.selvbetjeningopptjening.security.token.client;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.time.ExpirationChecker;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenAccessParam;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenData;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasLength;

/**
 * Obtains access tokens from an authorization server (AS), and caches them
 * to avoid unnecessary calls to the AS.
 */
public abstract class CacheAwareTokenClient extends Oauth2TokenClient {

    private static final int CLEANUP_TRIGGER = 1000;
    private final Map<String, Map<String, TokenData>> accessTokensByPidByAudience;
    private int cleanupNeed;

    public CacheAwareTokenClient(WebClient webClient,
                                 Oauth2ConfigGetter oauth2ConfigGetter,
                                 ExpirationChecker expirationChecker) {
        super(webClient, expirationChecker, oauth2ConfigGetter);
        this.accessTokensByPidByAudience = new HashMap<>();
    }

    public TokenData getTokenData(TokenAccessParam accessParam, String audience, String pid) {
        Optional<TokenData> validCachedToken = getValidTokenFromCache(audience, pid);

        if (validCachedToken.isPresent()) {
            return validCachedToken.get();
        }

        TokenData tokenData = getTokenData(accessParam, audience);
        occasionallyRemoveExpiredTokens();

        if (hasLength(pid)) {
            putInCache(audience, pid, tokenData);
        }

        return tokenData;
    }

    public void clearTokenData(String audience, String pid) {
        Map<String, TokenData> tokensByPid = accessTokensByPidByAudience.get(audience);

        if (tokensByPid != null) {
            tokensByPid.remove(pid);
        }
    }

    protected int getCleanupTrigger() {
        return CLEANUP_TRIGGER;
    }

    private void putInCache(String audience, String pid, TokenData tokenData) {
        Map<String, TokenData> tokensByPid = accessTokensByPidByAudience.get(audience);

        if (tokensByPid == null) {
            tokensByPid = new HashMap<>();
        }

        tokensByPid.put(pid, tokenData);
        accessTokensByPidByAudience.put(audience, tokensByPid);
    }

    private Optional<TokenData> getValidTokenFromCache(String audience, String pid) {
        if (!hasLength(pid)) {
            return Optional.empty();
        }

        Map<String, TokenData> tokensByPid = accessTokensByPidByAudience.get(audience);
        TokenData tokenData = tokensByPid == null ? null : tokensByPid.get(pid);

        if (tokenData == null) {
            return Optional.empty();
        }

        if (isExpired(tokenData)) {
            tokensByPid.remove(pid);
            return Optional.empty();
        }

        return Optional.of(tokenData);
    }

    /**
     * Removes expired tokens from cache.
     * To avoid performance degradation this is only done occasionally
     * (after a certain number of calls to getTokenData).
     */
    private void occasionallyRemoveExpiredTokens() {
        if (++cleanupNeed >= getCleanupTrigger()) {
            cleanupNeed = 0;
            removeExpiredTokens();
        }
    }

    private void removeExpiredTokens() {
        accessTokensByPidByAudience
                .keySet()
                .forEach(this::removeExpiredTokens);
    }

    private void removeExpiredTokens(String audience) {
        List<String> pidsOfTokensToBeRemoved = accessTokensByPidByAudience.get(audience)
                .entrySet()
                .stream()
                .filter(entry -> isExpired(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        for (String pid : pidsOfTokensToBeRemoved) {
            clearTokenData(audience, pid);
        }
    }
}
