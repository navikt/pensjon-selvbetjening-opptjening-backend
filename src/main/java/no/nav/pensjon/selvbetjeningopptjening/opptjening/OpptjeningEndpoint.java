package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfo;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

@RestController
@RequestMapping("api")
@ProtectedWithClaims(issuer = ISSUER)
public class OpptjeningEndpoint {

    private final Log log = LogFactory.getLog(getClass());
    private final OpptjeningProvider provider;
    private final LoginInfoGetter loginInfoGetter;

    public OpptjeningEndpoint(OpptjeningProvider provider, LoginInfoGetter loginInfoGetter) {
        this.provider = provider;
        this.loginInfoGetter = loginInfoGetter;
    }

    @GetMapping("/opptjening")
    public OpptjeningResponse getOpptjeningForFnr() {
        try {
            LoginInfo login = loginInfoGetter.getLoginInfo();
            return provider.calculateOpptjeningForFnr(login.getPid(), login.getSecurityLevel());
        } catch (PidValidationException e) {
            log.error(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (FailedCallingExternalServiceException e) {
            log.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
