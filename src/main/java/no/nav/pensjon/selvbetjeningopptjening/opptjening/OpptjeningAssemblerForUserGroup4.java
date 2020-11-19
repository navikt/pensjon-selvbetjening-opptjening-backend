package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OpptjeningAssemblerForUserGroup4 extends OpptjeningAssembler {

    public OpptjeningAssemblerForUserGroup4(UttaksgradGetter uttaksgradGetter) {
        super(uttaksgradGetter);
    }

    public OpptjeningResponse createResponse(LocalDate fodselsdato, OpptjeningBasis basis) {
        return createResponse(
                fodselsdato,
                basis.getPensjonspoengList(),
                basis.getPensjonsbeholdninger(),
                basis.getRestpensjoner(),
                basis.getUttaksgrader(),
                basis.getAfpHistorikk(),
                basis.getUforeHistorikk());
    }

    private OpptjeningResponse createResponse(LocalDate fodselsdato,
                                              List<Pensjonspoeng> pensjonspoengList,
                                              List<Beholdning> beholdninger,
                                              List<Restpensjon> restpensjoner,
                                              List<Uttaksgrad> uttaksgrader,
                                              AfpHistorikk afpHistorikk,
                                              UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse(fodselsdato.getYear());
        Map<Integer, Opptjening> opptjeningerByYear = getOpptjeningerByYear(pensjonspoengList, restpensjoner);
        populatePensjonspoeng(opptjeningerByYear, pensjonspoengList);
        populatePensjonsbeholdning(opptjeningerByYear, getBeholdningerByYear(beholdninger));

        if (opptjeningerByYear.isEmpty()) {
            return response;
        }

        int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
        int lastYearWithOpptjening = findLatestOpptjeningYear(opptjeningerByYear);
        setRestpensjoner(opptjeningerByYear, restpensjoner);
        removeFutureOpptjening(opptjeningerByYear, lastYearWithOpptjening);
        putYearsWithNoOpptjening(opptjeningerByYear, firstYearWithOpptjening, lastYearWithOpptjening);
        populateMerknadForOpptjening(opptjeningerByYear, beholdninger, uttaksgrader, afpHistorikk, uforeHistorikk);
        int numberOfYearsWithPensjonspoeng = countNumberOfYearsWithPensjonspoeng(opptjeningerByYear);
        setEndringerOpptjening(opptjeningerByYear, beholdninger);
        response.setNumberOfYearsWithPensjonspoeng(numberOfYearsWithPensjonspoeng);
        response.setOpptjeningData(toDto(opptjeningerByYear));
        return response;
    }
}
