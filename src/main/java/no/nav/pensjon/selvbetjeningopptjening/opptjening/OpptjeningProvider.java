package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
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

    OpptjeningResponse calculateOpptjeningForFnr(Pid pid, boolean isInternalUser) {
        LocalDate birthDate = getBirthDate(pid, isInternalUser);
        String fnr = pid.getPid();
        UserGroup userGroup = findUserGroup(birthDate);
        List<Uttaksgrad> uttaksgrader = uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(fnr);
        AfpHistorikk afpHistorikk = personConsumer.getAfpHistorikkForPerson(fnr);
        UforeHistorikk uforehistorikk = personConsumer.getUforeHistorikkForPerson(fnr);

        List<Restpensjon> restpensjoner = shouldGetRestpensjon(userGroup, uttaksgrader)
                ? restpensjonConsumer.getRestpensjonListe(fnr)
                : new ArrayList<>();

        return userGroup.getOpptjeningAssembler().apply(
                new OpptjeningArguments(
                        fnr,
                        birthDate,
                        restpensjoner,
                        uttaksgrader,
                        afpHistorikk,
                        uforehistorikk,
                        opptjeningsgrunnlagConsumer,
                        pensjonspoengConsumer,
                        pensjonsbeholdningConsumer,
                        uttaksgradGetter));
    }

    private LocalDate getBirthDate(Pid pid, boolean isInternalUser) {
        try {
            List<BirthDate> birthDates = pdlConsumer.getBirthDates(pid, isInternalUser);

            if (birthDates.isEmpty()) {
                log.warn("No birth info found in PDL for PID.");
                return getDefaultBirthdate(pid);
            }

            BirthDate birthdate = birthDates.get(0);

            if (birthdate.isBasedOnYearOnly()) {
                log.info("Birth date set to first day in birth year.");
            }

            return birthdate.getValue();
        } catch (Exception e) {
            log.error("Call to PDL failed: " + e.getMessage());
            return getDefaultBirthdate(pid);
        }
    }

    private LocalDate getDefaultBirthdate(Pid pid) {
        log.info("Deriving birth date directly from PID.");
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
