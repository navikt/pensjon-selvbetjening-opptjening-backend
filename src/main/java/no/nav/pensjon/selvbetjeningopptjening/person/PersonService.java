package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

@Service
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);
    private final PdlConsumer pdlConsumer;

    public PersonService(PdlConsumer pdlConsumer) {
        this.pdlConsumer = requireNonNull(pdlConsumer);
    }

    public Person getPerson(Pid pid, LoginSecurityLevel securityLevel) {
        try {
            return pdlConsumer.getPerson(pid, securityLevel);
        } catch (PdlException e) {
            handle(e, pid, securityLevel);
            return new Person(pid);
        } catch (Exception e) {
            log.error("Call to PDL failed: {}", e.getMessage(), e);
            return new Person(pid);
        }
    }

    private void handle(PdlException exception, Pid pid, LoginSecurityLevel securityLevel) {
        log.error("Call to PDL failed: {}", exception.getMessage(), exception);

        if ("unauthorized".equals(exception.getErrorCode())) {
            log.info("PID {} has login security {}", mask(pid), securityLevel.name());
        }
    }

    private static String mask(Pid pid) {
        return maskFnr(pid.getPid());
    }
}
