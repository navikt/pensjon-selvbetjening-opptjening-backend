package no.nav.pensjon.selvbetjeningopptjening.common.selftest;

import java.util.Optional;

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
     * @return optional information from ping-op
     */
    Optional<String> ping();

    /**
     * @return static information regarding the target
     */
    PingInfo getPingInfo();
}
