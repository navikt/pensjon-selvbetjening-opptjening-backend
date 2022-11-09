package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client for fetching OAuth2 metadata (configuration data) from Azure Active Directory.
 */
@Component
@Qualifier("azure-ad")
public class AzureAdOauth2MetadataClient extends Oauth2ConfigClient {

    public AzureAdOauth2MetadataClient(WebClient webClient,
                                       @Value("${azure-app.well-known-url}") String configUrl) {
        super(webClient, configUrl);
    }
}
