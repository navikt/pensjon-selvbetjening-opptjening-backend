package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.BeholdningClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.OpptjeningClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.PensjonspoengClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.client.RestpensjonClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.person.Person;
import no.nav.pensjon.selvbetjeningopptjening.person.PersonService;
import no.nav.pensjon.selvbetjeningopptjening.person.group.UserGroupUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpptjeningProvider {

    private final BeholdningClient pensjonsbeholdningConsumer;
    private final OpptjeningClient opptjeningsgrunnlagConsumer;
    private final PensjonspoengClient pensjonspoengConsumer;
    private final RestpensjonClient restpensjonConsumer;
    private final PersonConsumer personConsumer;
    private final PersonService personService;
    private final UttaksgradGetter uttaksgradGetter;

    public OpptjeningProvider(BeholdningClient pensjonsbeholdningConsumer,
                              OpptjeningClient opptjeningsgrunnlagConsumer,
                              PensjonspoengClient pensjonspoengConsumer,
                              RestpensjonClient restpensjonConsumer,
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

    public OpptjeningResponse calculateOpptjeningForFnr(Pid pid) {
        Person person = personService.getPerson(pid);
        String fnr = pid.getPid();
        UserGroup userGroup = UserGroupUtil.INSTANCE.findUserGroup(person.getFodselsdato());
        List<Uttaksgrad> uttaksgrader = uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(fnr);
        AfpHistorikk afpHistorikk = personConsumer.getAfpHistorikkForPerson(fnr);
        UforeHistorikk uforehistorikk = personConsumer.getUforeHistorikkForPerson(fnr);

        List<Restpensjon> restpensjoner = shouldGetRestpensjon(userGroup, uttaksgrader)
                ? restpensjonConsumer.getRestpensjonListe(pid)
                : new ArrayList<>();

        return userGroup.getOpptjeningAssembler().apply(
                new OpptjeningArguments(
                        person,
                        restpensjoner,
                        uttaksgrader,
                        afpHistorikk,
                        uforehistorikk,
                        opptjeningsgrunnlagConsumer,
                        pensjonspoengConsumer,
                        pensjonsbeholdningConsumer,
                        uttaksgradGetter));
    }

    private boolean shouldGetRestpensjon(UserGroup userGroup, List<Uttaksgrad> uttaksgrader) {
        return userGroup.hasRestpensjon() && userHasUttakAlderspensjonWithUttaksgradLessThan100(uttaksgrader);
    }

    private static boolean userHasUttakAlderspensjonWithUttaksgradLessThan100(List<Uttaksgrad> uttaksgrader) {
        return uttaksgrader.stream()
                .anyMatch(grad -> grad.getGrad() < 100);
    }
}