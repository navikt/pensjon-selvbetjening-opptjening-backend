package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientCredentials {

    private final String clientId;
    private final String clientSecret;

    public ClientCredentials(@Value("${azure-app.client-id}") String clientId,
                             @Value("${azure-app.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
