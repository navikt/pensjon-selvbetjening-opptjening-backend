package no.nav.pensjon.selvbetjeningopptjening.time;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime time();
}
