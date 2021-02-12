package no.nav.pensjon.selvbetjeningopptjening.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NowProvider implements TimeProvider {

    public LocalDateTime time() {
        return LocalDateTime.now();
    }
}
