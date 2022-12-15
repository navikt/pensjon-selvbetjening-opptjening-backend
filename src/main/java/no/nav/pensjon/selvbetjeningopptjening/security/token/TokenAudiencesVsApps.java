package no.nav.pensjon.selvbetjeningopptjening.security.token;

import java.util.List;
import java.util.Map;

/**
 * Holds which apps that use a given token audience when obtaining access tokens for other apps.
 * Note that the audience used here is the audience (or scope) used when requesting the access token
 * (so it is not necessarily the value of the 'aud' claim in the obtained token).
 */
public class TokenAudiencesVsApps {

    private final Map<String, List<String>> appListsByAudience;

    public TokenAudiencesVsApps(Map<String, List<String>> appListsByAudience) {
        this.appListsByAudience = appListsByAudience;
    }

    public Map<String, List<String>> getAppListsByAudience() {
        return appListsByAudience;
    }
}
