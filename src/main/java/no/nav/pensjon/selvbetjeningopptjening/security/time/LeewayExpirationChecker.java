package no.nav.pensjon.selvbetjeningopptjening.security.time;

import no.nav.pensjon.selvbetjeningopptjening.time.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LeewayExpirationChecker implements ExpirationChecker {

    private final TimeProvider timeProvider;
    private final long leewaySeconds;

    public LeewayExpirationChecker(TimeProvider timeProvider,
                                   @Value("${sts.token.expiration.leeway}") String leewaySeconds) {
        this.timeProvider = timeProvider;
        this.leewaySeconds = Long.parseLong(leewaySeconds);
    }

    public boolean isExpired(LocalDateTime issuedTime, Long expiresInSeconds) {
        LocalDateTime deadline = issuedTime.plusSeconds(expiresInSeconds - leewaySeconds);
        return timeProvider.time().isAfter(deadline);
    }

    public LocalDateTime time() {
        return timeProvider.time();
    }
}
