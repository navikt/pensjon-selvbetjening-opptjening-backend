package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.audit.CefEntry;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.SplitCookieAssembler;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

@RestController
@RequestMapping("api")
@Unprotected // not protected by token-service, but a custom protection is used (due to split ID token cookie)
public class OpptjeningOnBehalfEndpoint {

    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final Logger LOG = LoggerFactory.getLogger(OpptjeningOnBehalfEndpoint.class);
    private final OpptjeningProvider provider;
    private final JwsValidator jwsValidator;
    private final GroupChecker groupChecker;

    public OpptjeningOnBehalfEndpoint(OpptjeningProvider provider,
                                      JwsValidator jwsValidator,
                                      GroupChecker groupChecker) {
        this.provider = requireNonNull(provider);
        this.jwsValidator = requireNonNull(jwsValidator);
        this.groupChecker = requireNonNull(groupChecker);
    }

    @GetMapping("/opptjeningonbehalf")
    public OpptjeningResponse getOpptjeningForFnr(HttpServletRequest request,
                                                  @RequestParam(value = "fnr") String fnr) {
        LOG.info("Received on-behalf request for opptjening for fnr {}", maskFnr(fnr));

        try {
            String idToken = SplitCookieAssembler.getCookieValue(request, CookieType.INTERNAL_USER_ID_TOKEN);

            if (isEmpty(idToken)) {
                LOG.info("No JWT in request");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No JWT");
            }

            Jws<Claims> claims = jwsValidator.validate(idToken);
            var pid = new Pid(fnr, true);

            if (!isUserAllowed(pid, claims)) {
                return forbidden();
            }

            AUDIT.info(getAuditInfo(fnr, claims).format());
            return provider.calculateOpptjeningForFnr(pid, LoginSecurityLevel.INTERNAL);
        } catch (JwtException e) {
            LOG.error("JwtException. Message: {}.", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        } catch (PidValidationException e) {
            LOG.error("Invalid PID: {}. Message: {}.", maskFnr(fnr), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (FailedCallingExternalServiceException e) {
            LOG.error("Failed calling external service. Message: {}.", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private boolean isUserAllowed(Pid pid, Jws<Claims> claims) {
        var groupIds = (List<?>) claims.getBody().get("groups");

        if (groupIds == null) {
            return false;
        }

        List<AadGroup> groups = groupIds
                .stream()
                .filter(id -> AadGroup.exists(id.toString()))
                .map(id -> AadGroup.findById(id.toString()))
                .collect(toList());

        return groupChecker.isUserAuthorized(pid, groups);
    }

    private CefEntry getAuditInfo(String fnr, Jws<Claims> claims) {
        var navIdent = (String) claims.getBody().get("NAVident");
        LOG.info("NAV-ident: {}", navIdent == null ? "null" : navIdent.substring(0, 1) + "*****");

        return new CefEntry(
                ZonedDateTime.now().toInstant().toEpochMilli(),
                Level.INFO,
                "audit:access",
                "Datahenting paa vegne av",
                "Internbruker henter pensjonsopptjeningsdata for innbygger",
                navIdent,
                fnr);
    }

    private OpptjeningResponse forbidden() {
        String message = "User is not allowed";
        LOG.info(message);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
