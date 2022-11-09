package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import org.springframework.stereotype.Service;

@Service
public class TokenGetterFacade {

    private final ImpersonalEgressTokenService tokenGetter;

    public TokenGetterFacade(ImpersonalEgressTokenService tokenGetter) {
        this.tokenGetter = tokenGetter;
    }

    public String getToken(String appId) {
        return tokenGetter.getEgressTokenSuppliersByApp().get(appId).get().getValue();
    }
}
