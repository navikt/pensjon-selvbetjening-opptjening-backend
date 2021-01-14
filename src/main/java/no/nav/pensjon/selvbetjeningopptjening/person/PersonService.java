package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.security.masking.Masker.maskFnr;

@Service
public class PersonService {

    private final Log log = LogFactory.getLog(getClass());
    private final PdlConsumer pdlConsumer;

    public PersonService(PdlConsumer pdlConsumer) {
        this.pdlConsumer = pdlConsumer;
    }

    public void ping(){
        pdlConsumer.ping();
    }

    public LocalDate getBirthDate(Pid pid, LoginSecurityLevel securityLevel) {
        try {
            List<BirthDate> birthDates = pdlConsumer.getBirthDates(pid, securityLevel);

            if (birthDates.isEmpty()) {
                log.warn("No birth dates found for PID " + mask(pid));
                return getDefaultBirthdate(pid);
            }

            BirthDate birthDate = birthDates.get(0);

            if (birthDate.isBasedOnYearOnly()) {
                log.info("Birth date set to first day in birth year");
            }

            return birthDate.getValue();
        } catch (PdlException e) {
            handle(e, pid, securityLevel);
            return getDefaultBirthdate(pid);
        } catch (Exception e) {
            log.error("Call to PDL failed: " + e.getMessage());
            return getDefaultBirthdate(pid);
        }
    }

    private LocalDate getDefaultBirthdate(Pid pid) {
        log.info("Deriving birth date directly from PID");
        return pid.getFodselsdato();
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
