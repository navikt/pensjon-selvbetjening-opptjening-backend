package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.person.PersonService;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.nav.pensjon.selvbetjeningopptjening.util.UserGroupUtil.findUserGroup;

@Component
public class OpptjeningProvider {

    private final PensjonsbeholdningConsumer pensjonsbeholdningConsumer;
    private final OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer;
    private final PensjonspoengConsumer pensjonspoengConsumer;
    private final RestpensjonConsumer restpensjonConsumer;
    private final PersonConsumer personConsumer;
    private final PersonService personService;
    private final UttaksgradGetter uttaksgradGetter;

    public OpptjeningProvider(PensjonsbeholdningConsumer pensjonsbeholdningConsumer,
                              OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer,
                              PensjonspoengConsumer pensjonspoengConsumer,
                              RestpensjonConsumer restpensjonConsumer,
                              PersonConsumer personConsumer,
                              PersonService personService,
                              UttaksgradGetter uttaksgradGetter) {
        this.pensjonsbeholdningConsumer = pensjonsbeholdningConsumer;
        this.opptjeningsgrunnlagConsumer = opptjeningsgrunnlagConsumer;
        this.pensjonspoengConsumer = pensjonspoengConsumer;
        this.restpensjonConsumer = restpensjonConsumer;
        this.personConsumer = personConsumer;
        this.personService = personService;
        this.uttaksgradGetter = uttaksgradGetter;
    }

    OpptjeningResponse calculateOpptjeningForFnr(Pid pid, LoginSecurityLevel securityLevel) {
        LocalDate birthDate = personService.getBirthDate(pid, securityLevel);
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

    public String ping(){
        Optional<String> ping1 = pensjonsbeholdningConsumer.ping();
        Optional<String> ping2 = opptjeningsgrunnlagConsumer.ping();
        Optional<String> ping3 = personConsumer.ping();
        Optional<String> ping4 = pensjonspoengConsumer.ping() ;
        Optional<String> ping5 = restpensjonConsumer.ping();
        Optional<String> ping6 = uttaksgradGetter.ping();
        return "ping";
    }

    private boolean shouldGetRestpensjon(UserGroup userGroup, List<Uttaksgrad> uttaksgrader) {
        return userGroup.hasRestpensjon() && userHasUttakAlderspensjonWithUttaksgradLessThan100(uttaksgrader);
    }

    private static boolean userHasUttakAlderspensjonWithUttaksgradLessThan100(List<Uttaksgrad> uttaksgrader) {
        return uttaksgrader.stream()
                .anyMatch(grad -> grad.getUttaksgrad() < 100);
    }
}
