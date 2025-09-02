package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.OAuth2TokenClient
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.set

/**
 * Obtains access tokens and caches them.
 * The cache is organised according to users and scopes.
 * A "user" may in this context be a person or a system.
 * The scope identifies which service/system the token is intended to be used for.
 */
abstract class CacheAwareTokenClient(
    tokenEndpoint: String,
    webClientBuilder: WebClient.Builder,
    retryAttempts: String,
    expirationChecker: ExpirationChecker
) : OAuth2TokenClient(
    tokenEndpoint,
    webClientBuilder,
    retryAttempts,
    expirationChecker
) {
    private var accessTokensByUserByScope: MutableMap<String, MutableMap<String, TokenData>> = HashMap()
    private var cleanupNeed = 0

    fun getTokenData(accessParameter: TokenAccessParameter, scope: String, user: String): TokenData {
        val validCachedToken = getValidTokenFromCache(scope, user)

        if (validCachedToken.isPresent) {
            return validCachedToken.get()
        }

        val tokenData = getTokenData(accessParameter, scope)
        occasionallyRemoveExpiredTokens()

        if (hasLength(user)) {
            putInCache(scope, user, tokenData)
        }

        return tokenData
    }

    fun clearTokenData(scope: String, user: String) {
        accessTokensByUserByScope[scope]?.remove(user)
    }

    open fun getCleanupTrigger(): Int = CLEANUP_TRIGGER

    private fun putInCache(scope: String, user: String, tokenData: TokenData) {
        val tokensByUser = accessTokensByUserByScope[scope] ?: HashMap()
        tokensByUser[user] = tokenData
        accessTokensByUserByScope[scope] = tokensByUser
    }

    private fun getValidTokenFromCache(scope: String, user: String): Optional<TokenData> {
        if (!hasLength(user)) {
            return Optional.empty()
        }

        val tokensByUser = accessTokensByUserByScope[scope]
        val tokenData = tokensByUser?.get(user) ?: return Optional.empty()

        if (isExpired(tokenData)) {
            tokensByUser.remove(user)
            return Optional.empty()
        }

        return Optional.of(tokenData)
    }

    /**
     * Removes expired tokens from the cache.
     * To avoid performance degradation, this is only done occasionally
     * (after a certain number of calls to getTokenData).
     */
    private fun occasionallyRemoveExpiredTokens() {
        if (++cleanupNeed >= getCleanupTrigger()) {
            cleanupNeed = 0
            removeExpiredTokens()
        }
    }

    private fun removeExpiredTokens() {
        accessTokensByUserByScope.keys.forEach { removeExpiredTokens(it) }
    }

    private fun removeExpiredTokens(scope: String) {
        val usersOfTokensToBeRemoved = accessTokensByUserByScope[scope]!!
            .entries
            .filter { isExpired(it.value) }
            .map { it.key }

        for (user in usersOfTokensToBeRemoved) {
            clearTokenData(scope, user)
        }
    }

    companion object {
        private const val CLEANUP_TRIGGER = 1000
    }
}
