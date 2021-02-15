package no.nav.pensjon.selvbetjeningopptjening.security.time;

import java.time.LocalDateTime;

public interface ExpirationChecker {

    boolean isExpired(LocalDateTime issuedTime, Long expiresInSeconds);

    LocalDateTime time();
}
