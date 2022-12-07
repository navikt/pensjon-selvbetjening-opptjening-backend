package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.getAttribute;
import static no.nav.pensjon.selvbetjeningopptjening.security.filter.FilterChainUtil.setAttribute;

/**
 * Obtains outgoing (egress) authorization tokens for accessing upstream services.
 */
@Component
@Order(2)
public class Filter02EgressToken implements Filter {

    private static final Logger log = LoggerFactory.getLogger(Filter02EgressToken.class);
    private final EgressAccessTokenFacade egressTokenGetter;
    private final TokenAudiencesVsApps audiencesVsApps;

    public Filter02EgressToken(EgressAccessTokenFacade egressTokenGetter, TokenAudiencesVsApps audiencesVsApps) {
        this.egressTokenGetter = egressTokenGetter;
        this.audiencesVsApps = audiencesVsApps;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        FilterChainData chainData = getAttribute(request);

        if (chainData.requestIsForUnprotectedResource() || chainData.skipEgressTokens()) {
            chain.doFilter(request, response);
            return;
        }

        TokenInfo ingressTokenInfo = chainData.ingressTokenInfo();
        log.debug("Getting egress token suppliers...");
        Map<String, Supplier<RawJwt>> personalAccessTokenSuppliersByApp = getPersonalEgressTokenSuppliersByApp(ingressTokenInfo);
        Map<String, Supplier<RawJwt>> impersonalAccessTokenSuppliersByApp = getImpersonalEgressTokenSuppliersByApp();
        log.debug("{} egress token suppliers obtained", personalAccessTokenSuppliersByApp.size());

        setAttribute(request, chainData
                .withPersonalEgressTokenSuppliersByApp(personalAccessTokenSuppliersByApp)
                .withImpersonalEgressTokenSuppliersByApp(impersonalAccessTokenSuppliersByApp));

        chain.doFilter(request, response);
    }

    private Map<String, Supplier<RawJwt>> getPersonalEgressTokenSuppliersByApp(TokenInfo ingressTokenInfo) {
        Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp = new HashMap<>();

        audiencesVsApps.getAppListsByAudience().forEach((audience, appIds)
                -> obtainPersonalTokenSupplier(ingressTokenInfo, audience, appIds, egressTokenSuppliersByApp));

        return egressTokenSuppliersByApp;
    }

    private Map<String, Supplier<RawJwt>> getImpersonalEgressTokenSuppliersByApp() {
        Map<String, Supplier<RawJwt>> egressTokenSuppliersByApp = new HashMap<>();

        audiencesVsApps.getAppListsByAudience().forEach((audience, appIds)
                -> obtainImpersonalTokenSupplier(audience, appIds, egressTokenSuppliersByApp));

        return egressTokenSuppliersByApp;
    }

    /**
     * Obtains the supplier of the personal access token for the given audience (to be used to access the given apps).
     * The personal info is provided by the ingressTokenInfo argument.
     * The resulting token supplier is put into the tokenSuppliersByApp map.
     */
    private void obtainPersonalTokenSupplier(TokenInfo ingressTokenInfo,
                                             String audience,
                                             List<String> appIds,
                                             Map<String, Supplier<RawJwt>> tokenSuppliersByApp) {
        Supplier<RawJwt> tokenSupplier = () -> egressTokenGetter.getAccessToken(
                ingressTokenInfo.getJwt(), ingressTokenInfo.getUserId(), ingressTokenInfo.getUserType(), audience);

        appIds.forEach(appId -> tokenSuppliersByApp.put(appId, tokenSupplier));
    }

    /**
     * Obtains the supplier of the impersonal access token for the given audience (to be used to access the given apps).
     * The resulting token supplier is put into the tokenSuppliersByApp map.
     */
    private void obtainImpersonalTokenSupplier(String audience,
                                               List<String> appIds,
                                               Map<String, Supplier<RawJwt>> tokenSuppliersByApp) {
        Supplier<RawJwt> tokenSupplier = () -> egressTokenGetter.getAccessToken(UserType.APPLICATION, audience);
        appIds.forEach(appId -> tokenSuppliersByApp.put(appId, tokenSupplier));
    }
}
