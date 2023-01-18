package no.nav.pensjon.selvbetjeningopptjening.mock;

import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;

import java.util.Map;

public class RequestContextCreator {

    public static RequestContext createForExternal(String appId) {
        return RequestContext.forExternalUser(
                TokenInfo.valid("j.w.t", UserType.EXTERNAL, null, ""),
                EgressTokenSupplier.forExternalUser(
                        Map.of(appId, () -> new RawJwt("token1")),
                        Map.of(appId, () -> new RawJwt("token2"))));
    }

    public static RequestContext createForExternal() {
        return RequestContext.forExternalUser(
                TokenInfo.valid("j.w.t", UserType.EXTERNAL, null, ""),
                EgressTokenSupplier.forExternalUser(
                        Map.of("ignored", () -> new RawJwt("token1")),
                        Map.of("ignored", () -> new RawJwt("token2"))));
    }
}
