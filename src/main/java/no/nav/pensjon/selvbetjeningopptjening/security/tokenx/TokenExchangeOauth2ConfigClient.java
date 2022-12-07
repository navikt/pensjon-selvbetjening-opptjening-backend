package no.nav.pensjon.selvbetjeningopptjening.security.tokenx;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Qualifier("tokenx")
public class TokenExchangeOauth2ConfigClient extends Oauth2ConfigClient {

    public TokenExchangeOauth2ConfigClient(WebClient webClient,
                                           @Value("${tokenx.openid.well-known-url}") String configUrl) {
        super(webClient, configUrl);
    }
}
