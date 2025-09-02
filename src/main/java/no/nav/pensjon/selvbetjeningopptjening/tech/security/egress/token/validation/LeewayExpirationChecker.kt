package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.validation

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Checks expiration, allowing a certain leeway period.
 * If current time is after the start of the leeway period, the token shall be renewed,
 * even if it is not yet expired (this is to avoid 'false' expiry due to clock skew).
 */
@Component
class LeewayExpirationChecker(
    val timeProvider: TimeProvider,
    @Value("\${token.expiration.leeway}") leewaySeconds: String
) : ExpirationChecker {

    private val leeway: Long = leewaySeconds.toLong()

    override fun isExpired(issuedTime: LocalDateTime, expiresInSeconds: Long): Boolean {
        val deadline = issuedTime.plusSeconds(expiresInSeconds - leeway)
        return timeProvider.time().isAfter(deadline)
    }

    override fun time(): LocalDateTime {
        return timeProvider.time()
    }
}
