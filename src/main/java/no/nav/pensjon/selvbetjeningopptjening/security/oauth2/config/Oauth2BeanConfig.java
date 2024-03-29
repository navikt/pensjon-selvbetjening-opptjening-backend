package no.nav.pensjon.selvbetjeningopptjening.security.oauth2.config;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2BasicData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Oauth2BeanConfig {

    /**
     * The key to the JWT claim that holds the audience for external users (logging in via ID-porten).
     * ID-porten access tokens for NAV do not contain an 'aud' claim; instead the 'client_id' claim is used.
     */
    private static final String AUDIENCE_CLAIM_KEY_FOR_EXTERNAL_USERS = "client_id";

    /**
     * The key to the JWT claim that holds the user ID for external users logging in through ID-porten directly.
     * pid = personidentifikator - the Norwegian national ID number (fødselsnummer/d-nummer), ref.
     * https://docs.digdir.no/oidc_protocol_access_token.html
     */
    private static final String USER_ID_CLAIM_KEY_FOR_EXTERNAL_USERS = "pid";

    /**
     * The key to the JWT claim that holds the user ID for internal users (logging in via Azure AD).
     * oid = object ID of user, ref.
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens
     */
    private static final String USER_ID_CLAIM_KEY_FOR_INTERNAL_USERS = "oid";

    /**
     * The default key to the JWT claim that holds the audience.
     * aud = audience
     */
    private static final String DEFAULT_AUDIENCE_CLAIM_KEY = "aud";


    @Bean
    @Qualifier("external-user")
    Oauth2BasicData externalUserOauth2BasicData(
            @Value("${external-user.oauth2.idporten.well-known-url}") String wellKnownUrl,
            @Value("${external-user.oauth2.idporten.audience}") String acceptedAudience) {

        return new Oauth2BasicData(
                wellKnownUrl,
                acceptedAudience,
                DEFAULT_AUDIENCE_CLAIM_KEY,
                USER_ID_CLAIM_KEY_FOR_EXTERNAL_USERS,
                UserType.EXTERNAL);
    }

    @Bean
    @Qualifier("internal-user")
    Oauth2BasicData internalUserOauth2BasicData(
            @Value("${internal-user.oauth2.well-known-url}") String wellKnownUrl,
            @Value("${internal-user.oauth2.audience}") String acceptedAudience) {

        return new Oauth2BasicData(
                wellKnownUrl,
                acceptedAudience,
                DEFAULT_AUDIENCE_CLAIM_KEY,
                USER_ID_CLAIM_KEY_FOR_INTERNAL_USERS,
                UserType.INTERNAL);
    }
}
