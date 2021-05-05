package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

@Service
public class PersonService {

    private final Log log = LogFactory.getLog(getClass());
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
            log.error("Call to PDL failed: " + e.getMessage());
            return new Person(pid);
        }
    }

    private void handle(PdlException exception, Pid pid, LoginSecurityLevel securityLevel) {
        log.error("Call to PDL failed: " + exception.getMessage());

        if ("unauthorized".equals(exception.getErrorCode())) {
            log.info(String.format("PID %s has login security %s", mask(pid), securityLevel.name()));
        }
    }

    private static String mask(Pid pid) {
        return maskFnr(pid.getPid());
    }
}
