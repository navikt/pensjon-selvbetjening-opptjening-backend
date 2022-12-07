package no.nav.pensjon.selvbetjeningopptjening.security.token;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;

import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

/**
 * Wraps all the egress token suppliers used in this app.
 */
public record EgressTokenSupplier(
        Map<String, Supplier<RawJwt>> personalTokenSuppliersByApp,
        Map<String, Supplier<RawJwt>> impersonalTokenSuppliersByApp) {

    public EgressTokenSupplier withPersonal(Map<String, Supplier<RawJwt>> tokenSuppliersByApp) {
        return new EgressTokenSupplier(tokenSuppliersByApp, impersonalTokenSuppliersByApp);
    }

    public EgressTokenSupplier withImpersonal(Map<String, Supplier<RawJwt>> tokenSuppliersByApp) {
        return new EgressTokenSupplier(personalTokenSuppliersByApp, tokenSuppliersByApp);
    }

    public RawJwt getPersonalToken(AppIds service) {
        return personalTokenSuppliersByApp.get(service.appName).get();
    }

    public RawJwt getImpersonalToken(AppIds service) {
        return impersonalTokenSuppliersByApp.get(service.appName).get();
    }

    public static EgressTokenSupplier empty() {
        return new EgressTokenSupplier(emptyMap(), emptyMap());
    }

    public static EgressTokenSupplier forApplication(Map<String, Supplier<RawJwt>> impersonalTokenSuppliersByApp) {
        return new EgressTokenSupplier(emptyMap(), impersonalTokenSuppliersByApp);
    }

    public static EgressTokenSupplier forInternalUser(Map<String, Supplier<RawJwt>> personalTokenSuppliersByApp) {
        return new EgressTokenSupplier(personalTokenSuppliersByApp, emptyMap());
    }

    public static EgressTokenSupplier forExternalUser(Map<String, Supplier<RawJwt>> personalTokenSuppliersByApp,
                                                      Map<String, Supplier<RawJwt>> impersonalTokenSuppliersByApp) {
        return new EgressTokenSupplier(personalTokenSuppliersByApp, impersonalTokenSuppliersByApp);
    }
}
