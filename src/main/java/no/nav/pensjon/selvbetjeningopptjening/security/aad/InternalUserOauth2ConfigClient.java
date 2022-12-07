package no.nav.pensjon.selvbetjeningopptjening.security.aad;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Qualifier("internal-user")
public class InternalUserOauth2ConfigClient extends Oauth2ConfigClient {

    public InternalUserOauth2ConfigClient(WebClient webClient,
                                          @Value("${internal-user.oauth2.well-known-url}") String configUrl) {
        super(webClient, configUrl);
    }
}
