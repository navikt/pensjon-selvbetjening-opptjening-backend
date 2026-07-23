package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.person.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpptjeningAssemblerForUserGroup5 extends OpptjeningAssembler {
    private static final int ANDEL_PENSJON_BASERT_PA_BEHOLDNING_USERGROUP5 = 10;

    public OpptjeningResponse createResponse(Person person, OpptjeningBasis basis) {
        return createResponse(
                person,
                basis.getPensjonsbeholdninger(),
                basis.getRestpensjoner(),
                basis.getInntekter(),
                basis.getUttaksgrader(),
                basis.getUttaksgraderForBeholdningAfter2009(),
                basis.getAfpHistorikk(),
                basis.getUforeHistorikk());
    }

    private OpptjeningResponse createResponse(Person person,
                                              List<Beholdning> beholdninger,
                                              List<Restpensjon> restpensjoner,
                                              List<Inntekt> inntekter,
                                              List<Uttaksgrad> uttaksgrader,
                                              List<Uttaksgrad> uttaksgraderForBeholdningAfter2009,
                                              AfpHistorikk afpHistorikk,
                                              UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse(person, ANDEL_PENSJON_BASERT_PA_BEHOLDNING_USERGROUP5, person.getPid().getPid(), getFullmektigPid());
        Map<Integer, Opptjening> opptjeningerByYear = getOpptjeningerByYear(new ArrayList<>(), restpensjoner);
        Map<Integer, Long> inntekterByYear = getSumPensjonsgivendeInntekterByYear(inntekter);
        populatePensjonsbeholdning(opptjeningerByYear, getBeholdningerByYear(beholdninger));

        if (opptjeningerByYear.isEmpty()) {
            return response;
        }

        int firstYearWithOpptjening = getFirstYearWithOpptjening(person.getFodselsdato());
        int lastYearWithOpptjening = findLatestOpptjeningYear(opptjeningerByYear);
        setRestpensjoner(opptjeningerByYear, restpensjoner);
        removeFutureOpptjening(opptjeningerByYear, lastYearWithOpptjening);
        putYearsWithAdditionalInntekt(inntekterByYear, opptjeningerByYear, firstYearWithOpptjening, lastYearWithOpptjening);
        putYearsWithNoOpptjening(opptjeningerByYear, firstYearWithOpptjening, lastYearWithOpptjening);
        populateMerknadForOpptjening(opptjeningerByYear, beholdninger, uttaksgrader, afpHistorikk, uforeHistorikk);
        setEndringerOpptjening(opptjeningerByYear, beholdninger, uttaksgraderForBeholdningAfter2009);
        response.setOpptjeningData(toDto(opptjeningerByYear));
        return response;
    }
}
