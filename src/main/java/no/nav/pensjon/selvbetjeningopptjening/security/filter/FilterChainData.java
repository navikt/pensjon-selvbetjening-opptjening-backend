package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;

import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.util.StringUtils.hasText;

/**
 * Used to transfer data from one servlet filter to the next filters in the chain.
 */
public class FilterChainData {

    private final boolean requestForUnprotectedResource;
    private final boolean accessAsApplication;
    private final boolean skipEgressTokens;
    private final TokenInfo ingressTokenInfo;
    private final EgressTokenSupplier egressTokenSupplier;
    private final String fullmaktsgiverPid;
    private final boolean fullmaktEnabled;

    private FilterChainData(boolean requestForUnprotectedResource,
                            boolean accessAsApplication,
                            boolean skipEgressTokens,
                            TokenInfo ingressTokenInfo,
                            EgressTokenSupplier egressTokenSupplier,
                            String fullmaktsgiverPid) {
        this.requestForUnprotectedResource = requestForUnprotectedResource;
        this.accessAsApplication = accessAsApplication;
        this.skipEgressTokens = skipEgressTokens;
        this.ingressTokenInfo = ingressTokenInfo;
        this.egressTokenSupplier = egressTokenSupplier;
        this.fullmaktsgiverPid = fullmaktsgiverPid == null ? "" : fullmaktsgiverPid;
        this.fullmaktEnabled = hasText(fullmaktsgiverPid);
    }

    public static FilterChainData defaultInstance() {
        return new FilterChainData(false, false, false, TokenInfo.invalid(), EgressTokenSupplier.empty(), "");
    }

    public static FilterChainData instanceWhenRequestIsForUnprotectedResource() {
        return new FilterChainData(true, false, false, TokenInfo.invalid(), EgressTokenSupplier.empty(), "");
    }

    public FilterChainData withAccessAsApplication(boolean value) {
        // Access as application => skip egress tokens (since egress tokens require user context)
        return new FilterChainData(requestForUnprotectedResource, value, value, ingressTokenInfo, egressTokenSupplier, fullmaktsgiverPid);
    }

    public FilterChainData withSkipEgressTokens(boolean value) {
        return new FilterChainData(requestForUnprotectedResource, accessAsApplication, value, ingressTokenInfo, egressTokenSupplier, fullmaktsgiverPid);
    }

    public FilterChainData withIngressTokenInfo(TokenInfo value) {
        return new FilterChainData(requestForUnprotectedResource, accessAsApplication, skipEgressTokens, value, egressTokenSupplier, fullmaktsgiverPid);
    }

    public FilterChainData withPersonalEgressTokenSuppliersByApp(Map<String, Supplier<RawJwt>> value) {
        return new FilterChainData(requestForUnprotectedResource, accessAsApplication, skipEgressTokens, ingressTokenInfo, egressTokenSupplier.withPersonal(value), fullmaktsgiverPid);
    }

    public FilterChainData withImpersonalEgressTokenSuppliersByApp(Map<String, Supplier<RawJwt>> value) {
        return new FilterChainData(requestForUnprotectedResource, accessAsApplication, skipEgressTokens, ingressTokenInfo, egressTokenSupplier.withImpersonal(value), fullmaktsgiverPid);
    }

    public FilterChainData withFullmaktsgiverPid(String value) {
        return new FilterChainData(requestForUnprotectedResource, accessAsApplication, skipEgressTokens, ingressTokenInfo, egressTokenSupplier, value);
    }

    public boolean requestIsForUnprotectedResource() {
        return requestForUnprotectedResource;
    }

    public boolean accessIsAsApplication() {
        return accessAsApplication;
    }

    public boolean skipEgressTokens() {
        return skipEgressTokens;
    }

    public TokenInfo ingressTokenInfo() {
        return ingressTokenInfo;
    }

    public EgressTokenSupplier egressTokenSupplier() {
        return egressTokenSupplier;
    }

    public String fullmaktsgiverPid() {
        return fullmaktsgiverPid;
    }

    public boolean fullmaktEnabled() {
        return fullmaktEnabled;
    }
}
