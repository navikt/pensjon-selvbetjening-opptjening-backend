package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.security.CertificateGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.OidcSigningKeyResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("internal-user")
public class InternalUserSigningKeyResolver extends OidcSigningKeyResolver {

    public InternalUserSigningKeyResolver(
            @Qualifier("internal-user") CertificateGetter certificateGetter) {
        super(certificateGetter);
    }
}
