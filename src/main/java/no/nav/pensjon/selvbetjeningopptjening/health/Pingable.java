package no.nav.pensjon.selvbetjeningopptjening.health;

public interface Pingable {

    default boolean isEnabled() {
        return true;
    }

    void ping();

    PingInfo getPingInfo();
}
