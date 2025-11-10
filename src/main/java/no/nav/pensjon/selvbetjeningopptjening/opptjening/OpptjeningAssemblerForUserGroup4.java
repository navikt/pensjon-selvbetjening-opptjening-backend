package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.person.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OpptjeningAssemblerForUserGroup4 extends OpptjeningAssembler {

    public OpptjeningAssemblerForUserGroup4(UttaksgradGetter uttaksgradGetter /*, PidExtractor pidExtractor*/) {
        super(uttaksgradGetter);
    }

    public OpptjeningResponse createResponse(Person person, OpptjeningBasis basis) {
        return createResponse(
                person,
                basis.getPensjonspoengList(),
                basis.getPensjonsbeholdninger(),
                basis.getRestpensjoner(),
                basis.getUttaksgrader(),
                basis.getAfpHistorikk(),
                basis.getUforeHistorikk());
    }

    private OpptjeningResponse createResponse(Person person,
                                              List<Pensjonspoeng> pensjonspoengList,
                                              List<Beholdning> beholdninger,
                                              List<Restpensjon> restpensjoner,
                                              List<Uttaksgrad> uttaksgrader,
                                              AfpHistorikk afpHistorikk,
                                              UforeHistorikk uforeHistorikk) {
        LocalDate fodselsdato = person.getFodselsdato();
        OpptjeningResponse response = new OpptjeningResponse(person,
                calculateAndelNyttRegelverkUsergroup4(fodselsdato.getYear()), person.getPid().getPid(), getFullmektigPid());
        Map<Integer, Opptjening> opptjeningerByYear = getOpptjeningerByYear(pensjonspoengList, restpensjoner);
        populatePensjonspoeng(opptjeningerByYear, pensjonspoengList, uttaksgrader);
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

    private int calculateAndelNyttRegelverkUsergroup4(int fodselsaar) {
        return switch (fodselsaar) {
            case 1954 -> 1;
            case 1955 -> 2;
            case 1956 -> 3;
            case 1957 -> 4;
            case 1958 -> 5;
            case 1959 -> 6;
            case 1960 -> 7;
            case 1961 -> 8;
            case 1962 -> 9;
            default -> throw new IllegalStateException("Fodselsaar " + fodselsaar + " is not valid for Usergroup4");
        };
    }
}
