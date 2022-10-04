package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenGetterFacade {

    private final String cluster;
    private final ServiceTokenGetter legacyTokenGetter;
    private final ImpersonalEgressTokenService tokenGetter;

    public TokenGetterFacade(@Value("${nais.cluster.name}") String cluster,
                             ServiceTokenGetter legacyTokenGetter,
                             ImpersonalEgressTokenService tokenGetter) {
        this.cluster = cluster;
        this.legacyTokenGetter = legacyTokenGetter;
        this.tokenGetter = tokenGetter;
    }

    public String getToken(String appId) throws StsException {
        return "prod-fss".equals(cluster) || "dev-fss".equals(cluster)
                ? legacyTokenGetter.getServiceUserToken().getAccessToken()
                : tokenGetter.getEgressTokenSuppliersByApp().get(appId).get().getValue();
    }
}
