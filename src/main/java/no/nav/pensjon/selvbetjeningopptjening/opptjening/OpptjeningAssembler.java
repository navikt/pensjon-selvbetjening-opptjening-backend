package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter;
import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.model.code.OpptjeningTypeCode;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Math.round;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.BeholdningMapper.fromDto;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsbeholdningCalculator.calculatePensjonsbeholdningsendringer;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsopptjeningMapper.toDto;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.firstDayOf;

abstract class OpptjeningAssembler {

    private static final String INNTEKT_TYPE_SUM_PENSJONSGIVENDE_INNTEKT = "SUM_PI";
    private final UttaksgradGetter uttaksgradGetter;

    OpptjeningAssembler(UttaksgradGetter uttaksgradGetter) {
        this.uttaksgradGetter = uttaksgradGetter;
    }

    Map<Integer, OpptjeningDto> getOpptjeningerByYear(List<Pensjonspoeng> pensjonspoengList,
                                                      List<Restpensjon> restpensjoner) {
        Map<Integer, OpptjeningDto> opptjeningerByYear = new HashMap<>();
        pensjonspoengList.forEach(poeng -> putOpptjeningYear(opptjeningerByYear, poeng.getAr()));
        restpensjoner.forEach(pensjon -> putOpptjeningYear(opptjeningerByYear, pensjon.getFomDato().getYear()));
        return opptjeningerByYear;
    }

    Map<Integer, Long> getSumPensjonsgivendeInntekterByYear(List<Inntekt> inntekter) {
        return inntekter.stream()
                .filter(inntekt -> INNTEKT_TYPE_SUM_PENSJONSGIVENDE_INNTEKT.equals(inntekt.getInntektType()))
                .collect(toMap(Inntekt::getInntektAr, Inntekt::getBelop));
    }

    void populatePensjonsbeholdning(Map<Integer, OpptjeningDto> opptjeningerByYear,
                                    Map<Integer, BeholdningDto> beholdningerByYear) {
        beholdningerByYear.forEach((year, beholdning) -> populatePensjonsbeholdning(opptjeningerByYear, year, beholdning));

        beholdningerByYear.values().forEach(
                beholdning -> setPensjonsgivendeInntektFromInntektopptjeningBelop(opptjeningerByYear, beholdning));
    }

    Map<Integer, BeholdningDto> getBeholdningerByYear(List<BeholdningDto> beholdninger) {
        if (beholdninger == null) {
            return emptyMap();
        }

        Map<Integer, BeholdningDto> beholdningerByYear = new HashMap<>();

        for (BeholdningDto beholdning : beholdninger) {
            if (beholdning.getFomDato() != null) {
                beholdningerByYear.put(beholdning.getFomDato().getYear(), beholdning);
            }
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

    int findLatestOpptjeningYear(Map<Integer, OpptjeningDto> opptjeningerByYear) {
        int lastYearWithOpptjening = -1;
        int thisYear = LocalDate.now().getYear();

        for (Integer year : opptjeningerByYear.keySet()) {
            if (lastYearWithOpptjening < year && year <= thisYear) {
                lastYearWithOpptjening = year;
            }
        }

        return lastYearWithOpptjening > -1 ? lastYearWithOpptjening : thisYear;
    }

    void setRestpensjoner(Map<Integer, OpptjeningDto> opptjeningerByYear, List<Restpensjon> restpensjoner) {
        restpensjoner.forEach(pensjon -> setRestpensjon(opptjeningerByYear, pensjon));
    }

    void removeFutureOpptjening(Map<Integer, OpptjeningDto> opptjeningerByYear, int latestOpptjeningYear) {
        opptjeningerByYear.entrySet().stream()
                .filter(entry -> entry.getKey() > latestOpptjeningYear)
                .forEach(entry -> opptjeningerByYear.remove(entry.getKey()));
    }

    /**
     * Adds opptjening to opptjeningerByYear for years with inntekt but no beholdning.
     */
    void putYearsWithAdditionalInntekt(Map<Integer, Long> inntekterByYear,
                                       Map<Integer, OpptjeningDto> opptjeningerByYear,
                                       int firstYearWithOpptjening,
                                       int lastYearWithOpptjening) {
        IntStream.rangeClosed(firstYearWithOpptjening, lastYearWithOpptjening)
                .forEach(year -> putYearWithAdditionalInntekt(inntekterByYear, opptjeningerByYear, year));
    }

    void putYearsWithNoOpptjening(Map<Integer, OpptjeningDto> opptjeningerByYear,
                                  int firstYearWithOpptjening,
                                  int lastYearWithOpptjening) {
        if (firstYearWithOpptjening <= 0 || lastYearWithOpptjening <= 0) {
            return;
        }

        IntStream.rangeClosed(firstYearWithOpptjening, lastYearWithOpptjening)
                .forEach(year -> putYearWithNoOpptjening(opptjeningerByYear, year));
    }

    void populateMerknadForOpptjening(Map<Integer, OpptjeningDto> opptjeningerByYear,
                                      List<BeholdningDto> beholdninger,
                                      List<Uttaksgrad> uttaksgradHistorikk,
                                      AfpHistorikk afpHistorikk,
                                      UforeHistorikk uforeHistorikk) {
        opptjeningerByYear.forEach(
                (year, opptjening) -> addMerknaderOnOpptjening(
                        beholdninger, uttaksgradHistorikk, afpHistorikk, uforeHistorikk, year, opptjening));
    }

    void setEndringerOpptjening(Map<Integer, OpptjeningDto> opptjeningerByYear,
                                List<BeholdningDto> beholdninger) {
        List<Long> vedtakIds = getVedtakIdsForBeholdningAfter2009(beholdninger);
        List<Uttaksgrad> uttaksgraderForBeholdningAfter2009 = uttaksgradGetter.getUttaksgradForVedtak(vedtakIds);

        opptjeningerByYear.entrySet().stream()
                .filter(entry -> entry.getKey() >= REFORM_2010)
                .forEach(entry -> setEndringer(beholdninger, uttaksgraderForBeholdningAfter2009, entry.getKey(), entry.getValue()));
    }

    void populatePensjonspoeng(Map<Integer, OpptjeningDto> opptjeningerByYear, List<Pensjonspoeng> pensjonspoengList) {
        pensjonspoengList.stream()
                .filter(poeng -> poeng.getAr() != null)
                .forEach(poeng -> populatePensjonspoeng(opptjeningerByYear, poeng));
    }

    int countNumberOfYearsWithPensjonspoeng(Map<Integer, OpptjeningDto> opptjeningerByYear) {
        return (int) opptjeningerByYear.values().stream()
                .filter(opptjening -> isPositive(opptjening.getPensjonspoeng()))
                .count();
    }

    private static void addMerknaderOnOpptjening(List<BeholdningDto> beholdninger, List<Uttaksgrad> uttaksgrader,
                                                 AfpHistorikk afpHistorikk, UforeHistorikk uforeHistorikk,
                                                 Integer year, OpptjeningDto opptjening) {
        MerknadHandler.addMerknaderOnOpptjening(
                year, opptjening, fromDto(beholdninger), uttaksgrader, afpHistorikk, uforeHistorikk);
    }

    private static List<Long> getVedtakIdsForBeholdningAfter2009(List<BeholdningDto> beholdninger) {
        return beholdninger.stream()
                .filter(beholdning -> beholdning.getVedtakId() != null && beholdning.getFomDato().getYear() >= REFORM_2010 - 1)
                .map(BeholdningDto::getVedtakId)
                .collect(toList());
    }

    private static void populatePensjonspoeng(Map<Integer, OpptjeningDto> opptjeningerByYear, Pensjonspoeng poeng) {
        OpptjeningDto opptjening = opptjeningerByYear.get(poeng.getAr());
        populateOpptjeningMapWithPensjonspoeng(opptjening, poeng);
    }

    private static void populateOpptjeningMapWithPensjonspoeng(OpptjeningDto opptjening, Pensjonspoeng pensjonspoeng) {
        String pensjonspoengType = pensjonspoeng.getPensjonspoengType();

        if (isOpptjeningTypeInntekt(pensjonspoengType)) {
            opptjening.setPensjonspoeng(pensjonspoeng.getPoeng());
            opptjening.setPensjonsgivendeInntekt(pensjonspoeng.getInntekt().getBelop().intValue());
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

    private static void putOpptjeningYear(Map<Integer, OpptjeningDto> opptjeningerByYear, int year) {
        OpptjeningDto opptjening = getOpptjening(opptjeningerByYear, year);
        opptjeningerByYear.put(year, opptjening);
    }

    private static void putYearWithNoOpptjening(Map<Integer, OpptjeningDto> opptjeningerByYear, int year) {
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

    private static boolean isOmsorgspoengGreaterThanPensjonspoeng(OpptjeningDto opptjening) {
        boolean pensjonspoengIsNullButOmsorgspoengIsNotNull =
                opptjening.getOmsorgspoeng() != null &&
                        opptjening.getPensjonspoeng() == null;

        boolean bothOmsorgspoengAndPensjonspoengIsNotNull =
                opptjening.getOmsorgspoeng() != null &&
                        opptjening.getPensjonspoeng() != null;

        return pensjonspoengIsNullButOmsorgspoengIsNotNull
                || bothOmsorgspoengAndPensjonspoengIsNotNull
                && opptjening.getPensjonspoeng() < opptjening.getOmsorgspoeng();
    }

    private static void putYearWithAdditionalInntekt(Map<Integer, Long> inntekterByYear,
                                                     Map<Integer, OpptjeningDto> opptjeningerByYear,
                                                     int year) {
        if (opptjeningerByYear.containsKey(year) || !inntekterByYear.containsKey(year)) {
            return;
        }

        OpptjeningDto opptjening = getOpptjening(opptjeningerByYear, year);
        opptjening.setPensjonsgivendeInntekt(inntekterByYear.get(year).intValue());
        opptjeningerByYear.put(year, opptjening);
    }

    private static void setEndringer(List<BeholdningDto> beholdninger,
                                     List<Uttaksgrad> uttaksgrader,
                                     int year,
                                     OpptjeningDto opptjening) {
        List<EndringPensjonsopptjening> endringer = calculatePensjonsbeholdningsendringer(
                year,
                fromDto(beholdninger),
                uttaksgrader);

        opptjening.setEndringOpptjening(toDto(endringer));
    }

    private static void setRestpensjon(Map<Integer, OpptjeningDto> opptjeningerByYear, Restpensjon restpensjon) {
        OpptjeningDto opptjening = opptjeningerByYear.get(restpensjon.getFomDato().getYear());

        if (opptjening == null) {
            return;
        }

        opptjening.setRestpensjon(getBelop(restpensjon));
    }

    private static double getBelop(Restpensjon restpensjon) {
        double belop = 0D;

        if (restpensjon.getRestGrunnpensjon() != null) {
            belop += restpensjon.getRestGrunnpensjon();
        }

        if (restpensjon.getRestTilleggspensjon() != null) {
            belop += restpensjon.getRestTilleggspensjon();
        }

        if (isPositive(restpensjon.getRestPensjonstillegg())) {
            belop += restpensjon.getRestPensjonstillegg();
        }

        return belop;
    }

    private static void populatePensjonsbeholdning(Map<Integer, OpptjeningDto> opptjeningerByYear, int year, BeholdningDto beholdning) {
        OpptjeningDto opptjening = getOpptjening(opptjeningerByYear, year);
        opptjening.setPensjonsbeholdning(round(beholdning.getBelop()));

        if (!opptjeningerByYear.containsKey(year)) {
            opptjeningerByYear.put(year, opptjening);
        }

        setDefaultPensjonsgivendeInntektBasedOnBeholdningYear(beholdning, opptjeningerByYear);
    }

    private static void setDefaultPensjonsgivendeInntektBasedOnBeholdningYear(BeholdningDto beholdning, Map<Integer, OpptjeningDto> opptjeningerByYear) {
        int currentYear = LocalDate.now().getYear();
        int beholdningYear = beholdning.getFomDato().getYear();

        if (beholdningYear < currentYear - 1) {
            Optional<OpptjeningDto> opptjening = Optional.ofNullable(opptjeningerByYear.get(beholdningYear));
            opptjening.ifPresent(opt -> opt.setPensjonsgivendeInntekt(0));
        }
    }

    private static void setPensjonsgivendeInntektFromInntektopptjeningBelop(Map<Integer, OpptjeningDto> opptjeningerByYear, BeholdningDto beholdning) {
        InntektOpptjeningBelop belop = beholdning.getInntektOpptjeningBelop();

        if (belop == null || belop.getSumPensjonsgivendeInntekt() == null) {
            return;
        }

        Integer year = belop.getAr();
        Optional<OpptjeningDto> opptjening = Optional.ofNullable(opptjeningerByYear.get(year));
        opptjening.ifPresent(opt -> opt.setPensjonsgivendeInntekt(belop.getSumPensjonsgivendeInntekt().getBelop().intValue()));
    }

    private static OpptjeningDto getOpptjening(Map<Integer, OpptjeningDto> opptjeningerByYear, int year) {
        return opptjeningerByYear.getOrDefault(year, new OpptjeningDto());
    }

    private static List<String> omsorgspoengOpptjeningTypes() {
        return List.of(
                OpptjeningTypeCode.OBO7H.toString(),
                OpptjeningTypeCode.OBU7.toString(),
                OpptjeningTypeCode.OSFE.toString(),
                OpptjeningTypeCode.OBO6H.toString(),
                OpptjeningTypeCode.OBU6.toString());
    }

    private static OpptjeningDto noOpptjening() {
        var opptjening = new OpptjeningDto();
        opptjening.setPensjonsgivendeInntekt(0);
        opptjening.setPensjonspoeng(0D);
        return opptjening;
    }

    private static boolean isPositive(Double value) {
        return value != null && value > 0;
    }
}
