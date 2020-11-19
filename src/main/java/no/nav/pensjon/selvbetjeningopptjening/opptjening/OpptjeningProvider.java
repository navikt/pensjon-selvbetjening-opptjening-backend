package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlRequest;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.util.UserGroupUtil.findUserGroup;

@Component
public class OpptjeningProvider {

    private final Log log = LogFactory.getLog(getClass());
    private final PensjonsbeholdningConsumer pensjonsbeholdningConsumer;
    private final OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer;
    private final PensjonspoengConsumer pensjonspoengConsumer;
    private final RestpensjonConsumer restpensjonConsumer;
    private final PersonConsumer personConsumer;
    private final PdlConsumer pdlConsumer;
    private final UttaksgradGetter uttaksgradGetter;

    public OpptjeningProvider(PensjonsbeholdningConsumer pensjonsbeholdningConsumer,
                              OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer,
                              PensjonspoengConsumer pensjonspoengConsumer,
                              RestpensjonConsumer restpensjonConsumer,
                              PersonConsumer personConsumer,
                              PdlConsumer pdlConsumer,
                              UttaksgradGetter uttaksgradGetter) {
        this.pensjonsbeholdningConsumer = pensjonsbeholdningConsumer;
        this.opptjeningsgrunnlagConsumer = opptjeningsgrunnlagConsumer;
        this.pensjonspoengConsumer = pensjonspoengConsumer;
        this.restpensjonConsumer = restpensjonConsumer;
        this.personConsumer = personConsumer;
        this.pdlConsumer = pdlConsumer;
        this.uttaksgradGetter = uttaksgradGetter;
    }

    OpptjeningResponse calculateOpptjeningForFnr(Pid pid) {
        LocalDate fodselsdato = getFodselsdato(pid);
        String fnr = pid.getPid();
        UserGroup userGroup = findUserGroup(fodselsdato);
        List<Uttaksgrad> uttaksgrader = uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(fnr);
        AfpHistorikk afpHistorikk = personConsumer.getAfpHistorikkForPerson(fnr);
        UforeHistorikk uforehistorikk = personConsumer.getUforeHistorikkForPerson(fnr);

        List<Restpensjon> restpensjoner = shouldGetRestpensjon(userGroup, uttaksgrader)
                ? restpensjonConsumer.getRestpensjonListe(fnr)
                : new ArrayList<>();

        return userGroup.getOpptjeningAssembler().apply(
                new OpptjeningArguments(
                        fnr,
                        fodselsdato,
                        restpensjoner,
                        uttaksgrader,
                        afpHistorikk,
                        uforehistorikk,
                        opptjeningsgrunnlagConsumer,
                        pensjonspoengConsumer,
                        pensjonsbeholdningConsumer,
                        uttaksgradGetter));
    }

    private LocalDate getFodselsdato(Pid pid) {
        try {
            PdlRequest request = new PdlRequest(pid.getPid());
            List<Foedsel> foedsler = pdlConsumer.getPdlResponse(request).getData().getHentPerson().getFoedsel();

            if (foedsler == null || foedsler.isEmpty()) {
                log.warn("No fødsel found in PDL for fnr. Deriving fødselsdato directly from fnr.");
                return pid.getFodselsdato();
            }

            Foedsel foedsel = foedsler.get(0);

            if (foedsel.getFoedselsdato() != null) {
                return foedsel.getFoedselsdato();
            }

            log.warn("No fødselsdato found in PDL for fnr.");

            if (foedsel.getFoedselsaar() != null) {
                log.info("Fødselsdato set to first day in fødselsår.");
                return LocalDate.of(foedsel.getFoedselsaar(), 1, 1);
            }

            log.warn("No fødselsår found in PDL for fnr.");
        } catch (Exception e) {
            log.error("Call to PDL failed.");
        }

        log.info("Deriving fødselsdato directly from fnr.");
        return pid.getFodselsdato();
    }

    private boolean shouldGetRestpensjon(UserGroup userGroup, List<Uttaksgrad> uttaksgrader) {
        return userGroup.hasRestpensjon() && userHasUttakAlderspensjonWithUttaksgradLessThan100(uttaksgrader);
    }

    private static boolean userHasUttakAlderspensjonWithUttaksgradLessThan100(List<Uttaksgrad> uttaksgrader) {
        return uttaksgrader.stream()
                .anyMatch(grad -> grad.getUttaksgrad() < 100);
    }
}
