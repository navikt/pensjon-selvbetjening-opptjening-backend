package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.ClientCredentialsAccessTokenService;
import no.nav.pensjon.selvbetjeningopptjening.security.token.AccessTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.security.token.client.CacheAwareTokenClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfiguration {

    /**
     * Specifies the relations between audiences and applications.
     * Used to obtain the appropriate token for accessing a given application.
     */
    @Bean
    public TokenAudiencesVsApps tokenAudiencesVsApps(
            @Value("${pensjon.representasjon.app-id}") String fullmaktAppId,
            @Value("${pdl.app-id}") String pdlAppId,
            @Value("${pensjonsopptjening-register-app-id}") String pensjonsopptjeningRegisterAppId,
            @Value("${skjermede-personer-pip-app-id}") String skjermedePersonerPipAppId,
            @Value("${pen.app-id}") String penAppId) {
        return new TokenAudiencesVsApps(
                Map.of(fullmaktAppId, List.of(AppIds.FULLMAKT.appName),
                        pdlAppId, List.of(AppIds.PERSONDATALOSNINGEN.appName),
                        pensjonsopptjeningRegisterAppId, List.of(AppIds.PENSJONSOPPTJENING_REGISTER.appName),
                        skjermedePersonerPipAppId, List.of(AppIds.SKJERMEDE_PERSONER_PIP.appName),
                        penAppId, List.of(AppIds.PENSJONSFAGLIG_KJERNE.appName)));
    }

    @Bean
    @Qualifier("application")
    public AccessTokenGetter clientCredentialsAccessTokenGetter(@Qualifier("client-credentials") CacheAwareTokenClient tokenGetter) {
        return new ClientCredentialsAccessTokenService(tokenGetter);
    }
}
