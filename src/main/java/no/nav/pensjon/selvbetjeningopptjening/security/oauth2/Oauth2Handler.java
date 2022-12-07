package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import no.nav.pensjon.selvbetjeningopptjening.security.PublicKeyBasisGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;

public class Oauth2Handler {

    private final PublicKeyBasisGetter publicKeyBasisGetter;
    private final String acceptedAudience;
    private final String audienceClaimKey;
    private final String userIdClaimKey;
    private final UserType userType;

    public Oauth2Handler(PublicKeyBasisGetter publicKeyBasisGetter,
                         String acceptedAudience,
                         String audienceClaimKey,
                         String userIdClaimKey,
                         UserType userType) {
        this.publicKeyBasisGetter = publicKeyBasisGetter;
        this.acceptedAudience = acceptedAudience;
        this.audienceClaimKey = audienceClaimKey;
        this.userIdClaimKey = userIdClaimKey;
        this.userType = userType;
    }

    public PublicKeyBasisGetter getPublicKeyBasisGetter() {
        return publicKeyBasisGetter;
    }

    public String getAcceptedAudience() {
        return acceptedAudience;
    }

    public String getAudienceClaimKey() {
        return audienceClaimKey;
    }

    public String getUserIdClaimKey() {
        return userIdClaimKey;
    }

    public UserType getUserType() {
        return userType;
    }
}
