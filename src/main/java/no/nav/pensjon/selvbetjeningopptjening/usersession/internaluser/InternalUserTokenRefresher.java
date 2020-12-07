package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenRefresher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("internal-user")
public class InternalUserTokenRefresher extends TokenRefresher {

    public InternalUserTokenRefresher(@Qualifier("internal-user") TokenGetter tokenGetter) {
        super(tokenGetter);
    }
}
