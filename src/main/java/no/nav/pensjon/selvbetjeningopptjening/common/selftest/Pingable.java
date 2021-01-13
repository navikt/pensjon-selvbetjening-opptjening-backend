package no.nav.pensjon.selvbetjeningopptjening.common.selftest;

/**
 * Defines that the implementing consumer has a ping operation
 */
public interface Pingable {

    /**
     * Ability to ignore certain, for instance if not yet taken into use.
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Perform the ping operation
     */
    void ping();

    /**
     * @return static information regarding the target
     */
    PingInfo getPingInfo();
}
