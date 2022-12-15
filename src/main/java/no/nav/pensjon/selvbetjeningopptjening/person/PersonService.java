package no.nav.pensjon.selvbetjeningopptjening.person;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);
    private final PdlConsumer pdlConsumer;

    public PersonService(PdlConsumer pdlConsumer) {
        this.pdlConsumer = requireNonNull(pdlConsumer);
    }

    public Person getPerson(Pid pid) {
        try {
            return pdlConsumer.getPerson(pid);
        } catch (PdlException e) {
            log.error("Call to PDL failed (error code {})", e.getErrorCode(), e);
            return new Person(pid);
        } catch (Exception e) {
            log.error("Call to PDL failed", e);
            return new Person(pid);
        }
    }
}
