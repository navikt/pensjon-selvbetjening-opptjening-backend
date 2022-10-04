package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Gets access token suppliers for impersonal service access (access as application).
 */
@Service
public class ImpersonalEgressTokenService {

    private static final Logger log = LoggerFactory.getLogger(ImpersonalEgressTokenService.class);
    private final EgressAccessTokenFacade egressTokenGetter;
    private final TokenAudiencesVsApps audiencesVsApps;

    public ImpersonalEgressTokenService(EgressAccessTokenFacade egressTokenGetter, TokenAudiencesVsApps audiencesVsApps) {
        this.egressTokenGetter = egressTokenGetter;
        this.audiencesVsApps = audiencesVsApps;
    }

    public Map<String, Supplier<RawJwt>> getEgressTokenSuppliersByApp() {
        log.debug("Getting impersonal egress token suppliers...");
        Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp = new HashMap<>();

        audiencesVsApps.getAppListsByAudience()
                .forEach((audience, appIds) -> obtainImpersonalTokenSupplier(audience, appIds, egressTokenSuppliersByApp));

        log.debug("{} impersonal egress token suppliers obtained", egressTokenSuppliersByApp.size());
        return egressTokenSuppliersByApp;
    }

    /**
     * Obtains the supplier of the impersonal access token for the given audience (to be used to access the given apps).
     * The resulting token supplier is put into the tokenSuppliersByApp map.
     */
    private void obtainImpersonalTokenSupplier(String audience,
                                               List<String> appIds,
                                               Map<String, Supplier<RawJwt>> tokenSuppliersByApp) {
        Supplier<RawJwt> tokenSupplier = () -> egressTokenGetter.getAccessToken(audience);
        appIds.forEach(appId -> tokenSuppliersByApp.put(appId, tokenSupplier));
    }
}
