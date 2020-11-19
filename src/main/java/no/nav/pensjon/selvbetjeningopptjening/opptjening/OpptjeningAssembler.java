package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.model.code.OpptjeningTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.OpptjeningMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.Math.round;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsbeholdningCalculator.calculatePensjonsbeholdningsendringer;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.firstDayOf;

abstract class OpptjeningAssembler {

    private static final String INNTEKT_TYPE_SUM_PENSJONSGIVENDE_INNTEKT = "SUM_PI";
    private final UttaksgradGetter uttaksgradGetter;

    OpptjeningAssembler(UttaksgradGetter uttaksgradGetter) {
        this.uttaksgradGetter = uttaksgradGetter;
    }

    Map<Integer, Opptjening> getOpptjeningerByYear(List<Pensjonspoeng> pensjonspoengList,
                                                   List<Restpensjon> restpensjoner) {
        Map<Integer, Opptjening> opptjeningerByYear = new HashMap<>();
        pensjonspoengList.forEach(poeng -> putOpptjeningYear(opptjeningerByYear, poeng.getYear()));
        restpensjoner.forEach(pensjon -> putOpptjeningYear(opptjeningerByYear, pensjon.getFomDate().getYear()));
        return opptjeningerByYear;
    }

    Map<Integer, Long> getSumPensjonsgivendeInntekterByYear(List<Inntekt> inntekter) {
        return inntekter.stream()
                .filter(inntekt -> INNTEKT_TYPE_SUM_PENSJONSGIVENDE_INNTEKT.equals(inntekt.getType()))
                .collect(toMap(Inntekt::getYear, Inntekt::getBelop));
    }

    void populatePensjonsbeholdning(Map<Integer, Opptjening> opptjeningerByYear,
                                    Map<Integer, Beholdning> beholdningerByYear) {
        beholdningerByYear.forEach((year, beholdning) -> populatePensjonsbeholdning(opptjeningerByYear, year, beholdning));

        beholdningerByYear.values().forEach(
                beholdning -> setPensjonsgivendeInntektFromInntektopptjeningBelop(opptjeningerByYear, beholdning));
    }

    Map<Integer, Beholdning> getBeholdningerByYear(List<Beholdning> beholdninger) {
        if (beholdninger == null) {
            return emptyMap();
        }

        Map<Integer, Beholdning> beholdningerByYear = new HashMap<>();

        for (Beholdning beholdning : beholdninger) {
            beholdningerByYear.put(beholdning.getFomDato().getYear(), beholdning);
        }

        return beholdningerByYear;
    }

    int getFirstYearWithOpptjening(LocalDate fodselsdato) {
        LocalDate firstYear = fodselsdato.plusYears(17);

        if (firstYear.isBefore(firstDayOf(1967))) {
            return 1967;
        }

        int fodselYear = fodselsdato.getYear();

        if (fodselsdato.isBefore(firstDayOf(1993))) {
            return fodselYear + 17;
        }

        if (fodselsdato.isBefore(firstDayOf(1994))) {
            return fodselYear + 16;
        }

        if (fodselsdato.isBefore(firstDayOf(1995))) {
            return fodselYear + 15;
        }

        if (fodselsdato.isBefore(firstDayOf(1996))) {
            return fodselYear + 14;
        }

        return fodselYear + 13;
    }

    int findLatestOpptjeningYear(Map<Integer, Opptjening> opptjeningerByYear) {
        int lastYearWithOpptjening = -1;
        int thisYear = LocalDate.now().getYear();

        for (Integer year : opptjeningerByYear.keySet()) {
            if (lastYearWithOpptjening < year && year <= thisYear) {
                lastYearWithOpptjening = year;
            }
        }

        return lastYearWithOpptjening > -1 ? lastYearWithOpptjening : thisYear;
    }

    void setRestpensjoner(Map<Integer, Opptjening> opptjeningerByYear, List<Restpensjon> restpensjoner) {
        restpensjoner.forEach(pensjon -> setRestpensjon(opptjeningerByYear, pensjon));
    }

    void removeFutureOpptjening(Map<Integer, Opptjening> opptjeningerByYear, int latestOpptjeningYear) {
        opptjeningerByYear.entrySet().stream()
                .filter(entry -> entry.getKey() > latestOpptjeningYear)
                .forEach(entry -> opptjeningerByYear.remove(entry.getKey()));
    }

    /**
     * Adds opptjening to opptjeningerByYear for years with inntekt but no beholdning.
     */
    void putYearsWithAdditionalInntekt(Map<Integer, Long> inntekterByYear,
                                       Map<Integer, Opptjening> opptjeningerByYear,
                                       int firstYearWithOpptjening,
                                       int lastYearWithOpptjening) {
        IntStream.rangeClosed(firstYearWithOpptjening, lastYearWithOpptjening)
                .forEach(year -> putYearWithAdditionalInntekt(inntekterByYear, opptjeningerByYear, year));
    }

    void putYearsWithNoOpptjening(Map<Integer, Opptjening> opptjeningerByYear,
                                  int firstYearWithOpptjening,
                                  int lastYearWithOpptjening) {
        if (firstYearWithOpptjening <= 0 || lastYearWithOpptjening <= 0) {
            return;
        }

        IntStream.rangeClosed(firstYearWithOpptjening, lastYearWithOpptjening)
                .forEach(year -> putYearWithNoOpptjening(opptjeningerByYear, year));
    }

    void populateMerknadForOpptjening(Map<Integer, Opptjening> opptjeningerByYear,
                                      List<Beholdning> beholdninger,
                                      List<Uttaksgrad> uttaksgradHistorikk,
                                      AfpHistorikk afpHistorikk,
                                      UforeHistorikk uforeHistorikk) {
        opptjeningerByYear.forEach(
                (year, opptjening) -> addMerknaderOnOpptjening(
                        beholdninger, uttaksgradHistorikk, afpHistorikk, uforeHistorikk, year, opptjening));
    }

    void setEndringerOpptjening(Map<Integer, Opptjening> opptjeningerByYear,
                                List<Beholdning> beholdninger) {
        List<Long> vedtakIds = getVedtakIdsForBeholdningAfter2009(beholdninger);
        List<Uttaksgrad> uttaksgraderForBeholdningAfter2009 = uttaksgradGetter.getUttaksgradForVedtak(vedtakIds);

        opptjeningerByYear.entrySet().stream()
                .filter(entry -> entry.getKey() >= REFORM_2010)
                .forEach(entry -> setEndringer(beholdninger, uttaksgraderForBeholdningAfter2009, entry.getKey(), entry.getValue()));
    }

    void populatePensjonspoeng(Map<Integer, Opptjening> opptjeningerByYear, List<Pensjonspoeng> pensjonspoengList) {
        pensjonspoengList.stream()
                .filter(Pensjonspoeng::hasYear)
                .forEach(poeng -> populatePensjonspoeng(opptjeningerByYear, poeng));
    }

    int countNumberOfYearsWithPensjonspoeng(Map<Integer, Opptjening> opptjeningerByYear) {
        return (int) opptjeningerByYear.values().stream()
                .filter(opptjening -> opptjening.getPensjonspoeng() > 0D)
                .count();
    }

    Map<Integer, OpptjeningDto> toDto(Map<Integer, Opptjening> opptjeningerByYear) {
        Map<Integer, OpptjeningDto> dtoMap = new HashMap<>();
        opptjeningerByYear.forEach((year, value) -> dtoMap.put(year, OpptjeningMapper.toDto(value)));
        return dtoMap;
    }

    private static void addMerknaderOnOpptjening(List<Beholdning> beholdninger, List<Uttaksgrad> uttaksgrader,
                                                 AfpHistorikk afpHistorikk, UforeHistorikk uforeHistorikk,
                                                 Integer year, Opptjening opptjening) {
        MerknadHandler.addMerknaderOnOpptjening(
                year, opptjening, beholdninger, uttaksgrader, afpHistorikk, uforeHistorikk);
    }

    private static List<Long> getVedtakIdsForBeholdningAfter2009(List<Beholdning> beholdninger) {
        return beholdninger.stream()
                .filter(beholdning -> beholdning.hasVedtak() && beholdning.getFomDato().getYear() >= REFORM_2010 - 1)
                .map(Beholdning::getVedtakId)
                .collect(toList());
    }

    private static void populatePensjonspoeng(Map<Integer, Opptjening> opptjeningerByYear, Pensjonspoeng poeng) {
        Opptjening opptjening = opptjeningerByYear.get(poeng.getYear());
        populateOpptjeningMapWithPensjonspoeng(opptjening, poeng);
    }

    private static void populateOpptjeningMapWithPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng) {
        String pensjonspoengType = pensjonspoeng.getType();

        if (isOpptjeningTypeInntekt(pensjonspoengType)) {
            opptjening.setPensjonspoeng(pensjonspoeng.getPoeng());
            opptjening.setPensjonsgivendeInntekt(pensjonspoeng.getInntekt().getBelop());
        }

        if (isOpptjeningTypeOmsorgspoeng(pensjonspoengType)) {
            opptjening.setOmsorgspoeng(pensjonspoeng.getPoeng());
            opptjening.setOmsorgspoengType(pensjonspoengType);
            MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng);
        }

        if (isOmsorgspoengGreaterThanPensjonspoeng(opptjening)) {
            opptjening.setPensjonspoeng(opptjening.getOmsorgspoeng());
        }

        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng);
    }

    private static void putOpptjeningYear(Map<Integer, Opptjening> opptjeningerByYear, int year) {
        Opptjening opptjening = getOpptjening(opptjeningerByYear, year);
        opptjeningerByYear.put(year, opptjening);
    }

    private static void putYearWithNoOpptjening(Map<Integer, Opptjening> opptjeningerByYear, int year) {
        if (opptjeningerByYear.containsKey(year)) {
            return;
        }

        opptjeningerByYear.put(year, noOpptjening());
    }

    private static boolean isOpptjeningTypeInntekt(String opptjeningType) {
        return OpptjeningTypeCode.PPI.toString().equals(opptjeningType);
    }

    private static boolean isOpptjeningTypeOmsorgspoeng(String opptjeningType) {
        return omsorgspoengOpptjeningTypes().contains(opptjeningType);
    }

    private static boolean isOmsorgspoengGreaterThanPensjonspoeng(Opptjening opptjening) {
        boolean hasOmsorgspoengButNotPensjonspoeng =
                opptjening.hasOmsorgspoeng() && !opptjening.hasPensjonspoeng();

        boolean hasBothOmsorgspoengAndPensjonspoeng =
                opptjening.hasOmsorgspoeng() && opptjening.hasPensjonspoeng();

        return hasOmsorgspoengButNotPensjonspoeng
                || hasBothOmsorgspoengAndPensjonspoeng
                && opptjening.getPensjonspoeng() < opptjening.getOmsorgspoeng();
    }

    private static void putYearWithAdditionalInntekt(Map<Integer, Long> inntekterByYear,
                                                     Map<Integer, Opptjening> opptjeningerByYear,
                                                     int year) {
        if (opptjeningerByYear.containsKey(year) || !inntekterByYear.containsKey(year)) {
            return;
        }

        Opptjening opptjening = getOpptjening(opptjeningerByYear, year);
        opptjening.setPensjonsgivendeInntekt(inntekterByYear.get(year));
        opptjeningerByYear.put(year, opptjening);
    }

    private static void setEndringer(List<Beholdning> beholdninger,
                                     List<Uttaksgrad> uttaksgrader,
                                     int year,
                                     Opptjening opptjening) {
        List<EndringPensjonsopptjening> endringer = calculatePensjonsbeholdningsendringer(
                year,
                beholdninger,
                uttaksgrader);

        opptjening.setOpptjeningsendringer(endringer);
    }

    private static void setRestpensjon(Map<Integer, Opptjening> opptjeningerByYear, Restpensjon restpensjon) {
        Opptjening opptjening = opptjeningerByYear.get(restpensjon.getFomDate().getYear());

        if (opptjening == null) {
            return;
        }

        opptjening.setRestpensjon(getBelop(restpensjon));
    }

    private static double getBelop(Restpensjon restpensjon) {
        double belop = restpensjon.getRestGrunnpensjon()
                + restpensjon.getRestTilleggspensjon();

        if (restpensjon.getRestPensjonstillegg() > 0D) {
            belop += restpensjon.getRestPensjonstillegg();
        }

        return belop;
    }

    private static void populatePensjonsbeholdning(Map<Integer, Opptjening> opptjeningerByYear,
                                                   int year, Beholdning beholdning) {
        Opptjening opptjening = getOpptjening(opptjeningerByYear, year);
        opptjening.setPensjonsbeholdning(round(beholdning.getBelop()));

        if (!opptjeningerByYear.containsKey(year)) {
            opptjeningerByYear.put(year, opptjening);
        }

        setDefaultPensjonsgivendeInntektBasedOnBeholdningYear(beholdning, opptjeningerByYear);
    }

    private static void setDefaultPensjonsgivendeInntektBasedOnBeholdningYear(
            Beholdning beholdning, Map<Integer, Opptjening> opptjeningerByYear) {
        int currentYear = LocalDate.now().getYear();
        int beholdningYear = beholdning.getFomDato().getYear();

        if (beholdningYear < currentYear - 1) {
            Optional<Opptjening> opptjening = Optional.ofNullable(opptjeningerByYear.get(beholdningYear));
            opptjening.ifPresent(opt -> opt.setPensjonsgivendeInntekt(0L));
        }
    }

    private static void setPensjonsgivendeInntektFromInntektopptjeningBelop(
            Map<Integer, Opptjening> opptjeningerByYear, Beholdning beholdning) {
        Inntektsopptjening inntektsopptjening = beholdning.getInntektsopptjening();

        if (inntektsopptjening == null || inntektsopptjening.getSumPensjonsgivendeInntekt() == null) {
            return;
        }

        int year = inntektsopptjening.getYear();
        Optional<Opptjening> opptjening = Optional.ofNullable(opptjeningerByYear.get(year));
        opptjening.ifPresent(o -> o.setPensjonsgivendeInntekt(inntektsopptjening.getSumPensjonsgivendeInntekt().getBelop()));
    }

    private static Opptjening getOpptjening(Map<Integer, Opptjening> opptjeningerByYear, int year) {
        return opptjeningerByYear.getOrDefault(year, new Opptjening(null, null));
    }

    private static List<String> omsorgspoengOpptjeningTypes() {
        return List.of(
                OpptjeningTypeCode.OBO7H.toString(),
                OpptjeningTypeCode.OBU7.toString(),
                OpptjeningTypeCode.OSFE.toString(),
                OpptjeningTypeCode.OBO6H.toString(),
                OpptjeningTypeCode.OBU6.toString());
    }

    private static Opptjening noOpptjening() {
        return new Opptjening(0L, 0D);
    }
}
