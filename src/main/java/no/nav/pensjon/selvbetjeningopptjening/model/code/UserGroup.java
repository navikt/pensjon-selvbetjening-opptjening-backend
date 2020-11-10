package no.nav.pensjon.selvbetjeningopptjening.model.code;

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public enum UserGroup {

    USER_GROUP_1(false, UserGroup::getOpptjeningForUserGroups123),
    USER_GROUP_2(true, UserGroup::getOpptjeningForUserGroups123),
    USER_GROUP_3(true, UserGroup::getOpptjeningForUserGroups123),
    USER_GROUP_4(true, UserGroup::getOpptjeningForUserGroup4),
    USER_GROUP_5(true, UserGroup::getOpptjeningForUserGroup5);

    private final boolean hasRestpensjon;
    private final Function<OpptjeningArguments, OpptjeningResponse> opptjeningAssembler;

    UserGroup(boolean hasRestpensjon, Function<OpptjeningArguments, OpptjeningResponse> opptjeningAssembler) {
        this.hasRestpensjon = hasRestpensjon;
        this.opptjeningAssembler = opptjeningAssembler;
    }

    static OpptjeningResponse getOpptjeningForUserGroups123(OpptjeningArguments args) {
        return getOpptjeningForUserGroups123(
                args.getFnr(),
                args.getFodselsdato(),
                args.getRestpensjoner(),
                args.getUttaksgrader(),
                args.getAfpHistorikk(),
                args.getUforeHistorikk(),
                args.getPensjonspoengConsumer(),
                args.getUttaksgradGetter());
    }

    static OpptjeningResponse getOpptjeningForUserGroup4(OpptjeningArguments args) {
        return getOpptjeningForUserGroup4(
                args.getFnr(),
                args.getFodselsdato(),
                args.getRestpensjoner(),
                args.getUttaksgrader(),
                args.getAfpHistorikk(),
                args.getUforeHistorikk(),
                args.getPensjonspoengConsumer(),
                args.getBeholdningConsumer(),
                args.getUttaksgradGetter());
    }

    static OpptjeningResponse getOpptjeningForUserGroup5(OpptjeningArguments args) {
        return getOpptjeningForUserGroup5(
                args.getFnr(),
                args.getFodselsdato(),
                args.getRestpensjoner(),
                args.getUttaksgrader(),
                args.getAfpHistorikk(),
                args.getUforeHistorikk(),
                args.getOpptjeningsgrunnlagConsumer(),
                args.getBeholdningConsumer(),
                args.getUttaksgradGetter());
    }

    private static OpptjeningResponse getOpptjeningForUserGroups123(String fnr,
                                                                    LocalDate fodselsdato,
                                                                    List<Restpensjon> restpensjoner,
                                                                    List<Uttaksgrad> uttaksgrader,
                                                                    AfpHistorikk afpHistorikk,
                                                                    UforeHistorikk uforeHistorikk,
                                                                    PensjonspoengConsumer pensjonspoengConsumer,
                                                                    UttaksgradGetter uttaksgradGetter) {
        List<Pensjonspoeng> pensjonspoengList = pensjonspoengConsumer.getPensjonspoengListe(fnr);

        OpptjeningBasis basis = new OpptjeningBasis(
                pensjonspoengList,
                emptyList(), // No beholdninger
                restpensjoner,
                emptyList(), // No inntekter
                uttaksgrader,
                afpHistorikk,
                uforeHistorikk);

        return new OpptjeningAssemblerForUserGroups123(uttaksgradGetter).createResponse(fodselsdato, basis);
    }

    private static OpptjeningResponse getOpptjeningForUserGroup4(String fnr,
                                                                 LocalDate fodselsdato,
                                                                 List<Restpensjon> restpensjoner,
                                                                 List<Uttaksgrad> uttaksgrader,
                                                                 AfpHistorikk afpHistorikk,
                                                                 UforeHistorikk uforeHistorikk,
                                                                 PensjonspoengConsumer pensjonspoengConsumer,
                                                                 PensjonsbeholdningConsumer beholdningConsumer,
                                                                 UttaksgradGetter uttaksgradGetter) {
        List<BeholdningDto> beholdninger = beholdningConsumer.getPensjonsbeholdning(fnr);
        List<Pensjonspoeng> pensjonspoengList = pensjonspoengConsumer.getPensjonspoengListe(fnr);

        OpptjeningBasis basis = new OpptjeningBasis(
                pensjonspoengList,
                beholdninger,
                restpensjoner,
                emptyList(), // No inntekter
                uttaksgrader,
                afpHistorikk,
                uforeHistorikk);

        return new OpptjeningAssemblerForUserGroup4(uttaksgradGetter).createResponse(fodselsdato, basis);
    }

    private static OpptjeningResponse getOpptjeningForUserGroup5(String fnr,
                                                                 LocalDate fodselsdato,
                                                                 List<Restpensjon> restpensjoner,
                                                                 List<Uttaksgrad> uttaksgrader,
                                                                 AfpHistorikk afpHistorikk,
                                                                 UforeHistorikk uforeHistorikk,
                                                                 OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer,
                                                                 PensjonsbeholdningConsumer beholdningConsumer,
                                                                 UttaksgradGetter uttaksgradGetter) {
        List<BeholdningDto> beholdninger = beholdningConsumer.getPensjonsbeholdning(fnr);
        int firstPossibleInntektYear = fodselsdato.getYear() + 13;
        List<Inntekt> inntekter = opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(fnr, firstPossibleInntektYear, LocalDate.now().getYear());

        OpptjeningBasis basis = new OpptjeningBasis(
                emptyList(), // No pensjonspoeng
                beholdninger,
                restpensjoner,
                inntekter,
                uttaksgrader,
                afpHistorikk,
                uforeHistorikk);

        return new OpptjeningAssemblerForUserGroup5(uttaksgradGetter).createResponse(fodselsdato, basis);
    }

    public Function<OpptjeningArguments, OpptjeningResponse> getOpptjeningAssembler() {
        return opptjeningAssembler;
    }

    public boolean hasRestpensjon() {
        return hasRestpensjon;
    }
}
