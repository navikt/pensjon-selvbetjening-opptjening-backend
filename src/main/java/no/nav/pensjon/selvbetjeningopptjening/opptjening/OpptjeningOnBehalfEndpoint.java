package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import io.jsonwebtoken.JwtException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.security.group.EgenAnsattChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.http.CookieType;
import no.nav.pensjon.selvbetjeningopptjening.security.http.SplitCookieAssembler;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

@RestController
@RequestMapping("api")
@Unprotected // not protected by token-service, but a custom protection is used (due to split ID token cookie)
public class OpptjeningOnBehalfEndpoint {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final OpptjeningProvider provider;
    private final JwsValidator jwsValidator;
    private final GroupChecker groupChecker;
    private final EgenAnsattChecker egenAnsattChecker;

    public OpptjeningOnBehalfEndpoint(OpptjeningProvider provider, JwsValidator jwsValidator, GroupChecker groupChecker, EgenAnsattChecker egenAnsattChecker) {
        this.provider = requireNonNull(provider);
        this.jwsValidator = requireNonNull(jwsValidator);
        this.groupChecker = requireNonNull(groupChecker);
        this.egenAnsattChecker = egenAnsattChecker;
    }

    @GetMapping("/opptjeningonbehalf")
    public OpptjeningResponse getOpptjeningForFnr(HttpServletRequest request,
                                                  @RequestParam(value = "fnr") String fnr) {
        log.info("Received on-behalf request for opptjening for fnr {}", maskFnr(fnr));

        try {
            String idToken = SplitCookieAssembler.getCookieValue(request, CookieType.INTERNAL_USER_ID_TOKEN);

            if (StringUtils.isEmpty(idToken)) {
                log.info("No JWT in request");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No JWT");
            }

            jwsValidator.validate(idToken);
            String accessToken = SplitCookieAssembler.getCookieValue(request, CookieType.INTERNAL_USER_ACCESS_TOKEN);
            var pid = new Pid(fnr, true);
            boolean isEgenAnsatt = egenAnsattChecker.isEgenAnsatt(pid);

            if (!isUserAllowed(accessToken, isEgenAnsatt)) {
                log.info("User is not allowed");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The service is not made available for the specified user yet");
            }

            return provider.calculateOpptjeningForFnr(pid, LoginSecurityLevel.INTERNAL);
        } catch (JwtException e) {
            log.error("JwtException. Message: {}.", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        } catch (PidValidationException e) {
            log.error("Invalid PID: {}. Message: {}.", maskFnr(fnr), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (FailedCallingExternalServiceException e) {
            log.error("Failed calling external service. Message: {}.", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private boolean isUserAllowed(String accessToken, boolean isEgenAnsatt) {
        return groupChecker.isUserAuthorized(accessToken, isEgenAnsatt);
    }
}
