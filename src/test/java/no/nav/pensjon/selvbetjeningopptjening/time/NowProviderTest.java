package no.nav.pensjon.selvbetjeningopptjening.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NowProviderTest {

    @Test
    void time() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        LocalDateTime time = new NowProvider().time();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(before.isBefore(time));
        assertTrue(after.isAfter(time));
    }
}
