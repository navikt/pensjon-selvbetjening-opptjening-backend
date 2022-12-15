package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;

public class Oauth2BasicData {

    private final String wellKnownUrl;
    private final String acceptedAudience;
    private final String audienceClaimKey;
    private final String userIdClaimKey;
    private final UserType userType;

    public Oauth2BasicData(String wellKnownUrl,
                           String acceptedAudience,
                           String audienceClaimKey,
                           String userIdClaimKey,
                           UserType userType) {
        this.wellKnownUrl = wellKnownUrl;
        this.acceptedAudience = acceptedAudience;
        this.audienceClaimKey = audienceClaimKey;
        this.userIdClaimKey = userIdClaimKey;
        this.userType = userType;
    }

    String getWellKnownUrl() {
        return wellKnownUrl;
    }

    String getAcceptedAudience() {
        return acceptedAudience;
    }

    String getAudienceClaimKey() {
        return audienceClaimKey;
    }

    String getUserIdClaimKey() {
        return userIdClaimKey;
    }

    UserType getUserType() {
        return userType;
    }
}
