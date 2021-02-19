package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OpptjeningAssemblerForUserGroups123 extends OpptjeningAssembler {
    private static final int ANDEL_PENSJON_BASERT_PA_BEHOLDNING_USERGROUPS123 = 0;

    public OpptjeningAssemblerForUserGroups123(UttaksgradGetter uttaksgradGetter) {
        super(uttaksgradGetter);
    }

    public OpptjeningResponse createResponse(LocalDate fodselsdato, OpptjeningBasis basis) {
        return createResponse(
                fodselsdato,
                basis.getPensjonspoengList(),
                basis.getRestpensjoner(),
                basis.getUttaksgrader(),
                basis.getAfpHistorikk(),
                basis.getUforeHistorikk());
    }

    private OpptjeningResponse createResponse(LocalDate fodselsdato,
                                              List<Pensjonspoeng> pensjonspoengList,
                                              List<Restpensjon> restpensjoner,
                                              List<Uttaksgrad> uttaksgrader,
                                              AfpHistorikk afpHistorikk,
                                              UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse(fodselsdato.getYear(), ANDEL_PENSJON_BASERT_PA_BEHOLDNING_USERGROUPS123);
        Map<Integer, Opptjening> opptjeningerByYear = getOpptjeningerByYear(pensjonspoengList, restpensjoner);
        populatePensjonspoeng(opptjeningerByYear, pensjonspoengList, uttaksgrader);

        if (opptjeningerByYear.isEmpty()) {
            return response;
        }

        int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
        int lastYearWithOpptjening = findLatestOpptjeningYear(opptjeningerByYear);
        setRestpensjoner(opptjeningerByYear, restpensjoner);
        removeFutureOpptjening(opptjeningerByYear, lastYearWithOpptjening);
        putYearsWithNoOpptjening(opptjeningerByYear, firstYearWithOpptjening, lastYearWithOpptjening);
        populateMerknadForOpptjening(opptjeningerByYear, null, uttaksgrader, afpHistorikk, uforeHistorikk);
        response.setNumberOfYearsWithPensjonspoeng(countNumberOfYearsWithPensjonspoeng(opptjeningerByYear));
        response.setOpptjeningData(toDto(opptjeningerByYear));
        return response;
    }
}
