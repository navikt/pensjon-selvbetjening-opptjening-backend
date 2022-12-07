package no.nav.pensjon.selvbetjeningopptjening.usersession;

import static java.util.Objects.requireNonNull;

/**
 * Holds the URL to be used when logging in external users via NAV loginservice.
 * This service has legacy status, and is planned to be phased out.
 * To easily switch on and off use of this service, an 'enabled' flag is provided.
 */
public class LegacyLogin {

    private final boolean enabled;
    private final String url;

    public static LegacyLogin enabled(String url) {
        return new LegacyLogin(true, url);
    }

    public static LegacyLogin disabled() {
        return new LegacyLogin(false, "");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUrl() {
        return url;
    }

    private LegacyLogin(boolean enabled, String url) {
        this.enabled = enabled;
        this.url = requireNonNull(url);
    }
}
