package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.OidcCertificateGetter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Qualifier("internal-user")
public class InternalUserCertificateGetter extends OidcCertificateGetter {

    public InternalUserCertificateGetter(
            WebClient webClient,
            @Qualifier("internal-user") OidcConfigGetter oidcConfigGetter) {
        super(webClient, oidcConfigGetter);
    }
}
