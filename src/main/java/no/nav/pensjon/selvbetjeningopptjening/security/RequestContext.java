package no.nav.pensjon.selvbetjeningopptjening.security;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.token.EgressTokenSupplier;
import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.jwt.ClaimsUtil.getInternalUserId;
import static org.springframework.util.StringUtils.hasText;

/**
 * The context (user info etc.) of the request processing thread.
 * Important: Use try-with-resources when using this auto-closeable class. This will ensure that the context
 * is cleared when processing is finished. Otherwise, request context might be accidentally reused e.g. when using
 * thread pooling (where threads are reused).
 */
public class RequestContext implements AutoCloseable {

    private static final ThreadLocal<RequestContext> instance = new ThreadLocal<>();
    private final TokenInfo ingressTokenInfo;
    private final String subjectPid;
    private final String fullmektigPid;
    private final EgressTokenSupplier egressTokenSupplier;

    public static RequestContext forExternalUser(TokenInfo ingressTokenInfo,
                                                 EgressTokenSupplier egressTokenSupplier) {
        var context = new RequestContext(ingressTokenInfo, "", ingressTokenInfo.getUserId(), egressTokenSupplier);
        instance.set(context);
        return context;
    }

    public static RequestContext forExternalUserOnBehalf(TokenInfo ingressTokenInfo,
                                                         String subjectPid,
                                                         EgressTokenSupplier egressTokenSupplier) {
        var context = new RequestContext(ingressTokenInfo, ingressTokenInfo.getUserId(), subjectPid, egressTokenSupplier);
        instance.set(context);
        return context;
    }

    public static RequestContext forInternalUser(TokenInfo ingressTokenInfo,
                                                 String subjectPid,
                                                 EgressTokenSupplier egressTokenSupplier) {
        var context = new RequestContext(ingressTokenInfo, "", subjectPid, egressTokenSupplier);
        instance.set(context);
        return context;
    }

    public static RequestContext forInternalUserOnBehalf(TokenInfo ingressTokenInfo,
                                                         String fullmektigPid,
                                                         String subjectPid,
                                                         EgressTokenSupplier egressTokenSupplier) {
        var context = new RequestContext(ingressTokenInfo, fullmektigPid, subjectPid, egressTokenSupplier);
        instance.set(context);
        return context;
    }

    public static RequestContext forSelfTest(TokenInfo ingressTokenInfo,
                                             EgressTokenSupplier egressTokenSupplier) {
        var context = new RequestContext(ingressTokenInfo, "", "", egressTokenSupplier);
        instance.set(context);
        return context;
    }

    /**
     * Gets an access token for egress (outgoing) use, i.e. for accessing the service having the given ID.
     */
    public static RawJwt getEgressAccessToken(AppIds service) {
        RequestContext context = currentInstance();
        EgressTokenSupplier tokenSupplier = context.egressTokenSupplier;

        return userIsInternal(context) || userIsExternal(context) && service.supportsTokenX
                ? tokenSupplier.getPersonalToken(service)
                : tokenSupplier.getImpersonalToken(service);
    }

    public static String getSubjectPid() {
        return currentInstance().subjectPid;
    }

    public static String getFullmektigPid() {
        return currentInstance().fullmektigPid;
    }

    public static String getNavIdent() {
        return getInternalUserId(claims());
    }

    public static boolean userIsInternal() {
        return userIsInternal(currentInstance());
    }

    @Override
    public void close() {
        instance.remove();
    }

    private RequestContext(TokenInfo ingressTokenInfo,
                           String fullmektigPid,
                           String subjectPid,
                           EgressTokenSupplier egressTokenSupplier) {
        this.ingressTokenInfo = requireNonNull(ingressTokenInfo);
        this.subjectPid = requireNonNull(subjectPid);
        this.fullmektigPid = fullmektigPid == null ? "" : fullmektigPid;
        this.egressTokenSupplier = egressTokenSupplier;
    }

    private static RequestContext currentInstance() {
        RequestContext requestContext = instance.get();

        if (requestContext == null) {
            throw new SecurityException("No user context");
        }

        return requestContext;
    }

    private static boolean userIsExternal(RequestContext context) {
        return UserType.EXTERNAL.equals(context.ingressTokenInfo.getUserType());
    }

    private static boolean userIsInternal(RequestContext context) {
        return UserType.INTERNAL.equals(context.ingressTokenInfo.getUserType());
    }

    private static Claims claims() {
        return currentInstance().ingressTokenInfo.getClaims();
    }
}
