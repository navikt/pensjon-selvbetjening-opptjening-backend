package no.nav.pensjon.selvbetjeningopptjening.security.time;

import no.nav.pensjon.selvbetjeningopptjening.time.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LeewayExpirationCheckerTest {

    @Mock
    TimeProvider timeProvider;

    @Test
    void isExpired_returns_true_when_expired_no_leeway() {
        when(timeProvider.time()).thenReturn(LocalDateTime.MIN.plusSeconds(71));
        var checker = new LeewayExpirationChecker(timeProvider, "0");
        assertTrue(checker.isExpired(LocalDateTime.MIN.plusSeconds(10), 60L));
    }

    @Test
    void isExpired_returns_false_when_not_expired_no_leeway() {
        when(timeProvider.time()).thenReturn(LocalDateTime.MIN.plusSeconds(69));
        var checker = new LeewayExpirationChecker(timeProvider, "0");
        assertFalse(checker.isExpired(LocalDateTime.MIN.plusSeconds(10), 60L));
    }

    @Test
    void isExpired_returns_true_when_expired_due_to_leeway() {
        when(timeProvider.time()).thenReturn(LocalDateTime.MIN.plusSeconds(69));
        var checker = new LeewayExpirationChecker(timeProvider, "2");
        assertTrue(checker.isExpired(LocalDateTime.MIN.plusSeconds(10), 60L));
    }

    @Test
    void time_returns_timeProvider_time() {
        LocalDateTime time = LocalDateTime.MIN.plusSeconds(10);
        when(timeProvider.time()).thenReturn(time);
        var checker = new LeewayExpirationChecker(timeProvider, "1");
        assertEquals(time, checker.time());
    }
}
