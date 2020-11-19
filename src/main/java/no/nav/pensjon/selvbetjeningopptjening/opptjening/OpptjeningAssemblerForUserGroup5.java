package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpptjeningAssemblerForUserGroup5 extends OpptjeningAssembler {

    public OpptjeningAssemblerForUserGroup5(UttaksgradGetter uttaksgradGetter) {
        super(uttaksgradGetter);
    }

    public OpptjeningResponse createResponse(LocalDate fodselsdato, OpptjeningBasis basis) {
        return createResponse(
                fodselsdato,
                basis.getPensjonsbeholdninger(),
                basis.getRestpensjoner(),
                basis.getInntekter(),
                basis.getUttaksgrader(),
                basis.getAfpHistorikk(),
                basis.getUforeHistorikk());
    }

    private OpptjeningResponse createResponse(LocalDate fodselsdato,
                                              List<Beholdning> beholdninger,
                                              List<Restpensjon> restpensjoner,
                                              List<Inntekt> inntekter,
                                              List<Uttaksgrad> uttaksgrader,
                                              AfpHistorikk afpHistorikk,
                                              UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse(fodselsdato.getYear());
        Map<Integer, Opptjening> opptjeningerByYear = getOpptjeningerByYear(new ArrayList<>(), restpensjoner);
        Map<Integer, Long> inntekterByYear = getSumPensjonsgivendeInntekterByYear(inntekter);
        populatePensjonsbeholdning(opptjeningerByYear, getBeholdningerByYear(beholdninger));

        if (opptjeningerByYear.isEmpty()) {
            return response;
        }

        int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
        int lastYearWithOpptjening = findLatestOpptjeningYear(opptjeningerByYear);
        setRestpensjoner(opptjeningerByYear, restpensjoner);
        removeFutureOpptjening(opptjeningerByYear, lastYearWithOpptjening);
        putYearsWithAdditionalInntekt(inntekterByYear, opptjeningerByYear, firstYearWithOpptjening, lastYearWithOpptjening);
        putYearsWithNoOpptjening(opptjeningerByYear, firstYearWithOpptjening, lastYearWithOpptjening);
        populateMerknadForOpptjening(opptjeningerByYear, beholdninger, uttaksgrader, afpHistorikk, uforeHistorikk);
        setEndringerOpptjening(opptjeningerByYear, beholdninger);
        response.setOpptjeningData(toDto(opptjeningerByYear));
        return response;
    }
}
