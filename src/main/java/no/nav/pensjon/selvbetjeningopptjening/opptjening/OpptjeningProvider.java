package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlRequest;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.OpptjeningTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;
import no.nav.pensjon.selvbetjeningopptjening.util.FnrUtil;
import no.nav.pensjon.selvbetjeningopptjening.util.UserGroupUtil;

public class OpptjeningProvider {
    private static final Log LOGGER = LogFactory.getLog(OpptjeningProvider.class);
    private PensjonsbeholdningConsumer pensjonsbeholdningConsumer;
    private OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer;
    private PensjonspoengConsumer pensjonspoengConsumer;
    private RestpensjonConsumer restpensjonConsumer;
    private PersonConsumer personConsumer;
    private PdlConsumer pdlConsumer;
    private UttaksgradConsumer uttaksgradConsumer;
    private EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator;
    private MerknadHandler merknadHandler;

    public OpptjeningResponse calculateOpptjeningForFnr(String fnr) {
        LocalDate fodselsdato = getFodselsdato(fnr);
        UserGroup userGroup = UserGroupUtil.findUserGroup(fodselsdato);
        List<Restpensjon> restpensjonList = new ArrayList<>();

        List<Uttaksgrad> uttaksgradhistorikk = uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr);
        AfpHistorikk afphistorikk = personConsumer.getAfpHistorikkForPerson(fnr);
        UforeHistorikk uforehistorikk = personConsumer.getUforeHistorikkForPerson(fnr);

        if (shouldGetRestpensjon(userGroup, uttaksgradhistorikk)) {
            restpensjonList = restpensjonConsumer.getRestpensjonListe(fnr);
        }

        if (UserGroup.USER_GROUP_5.equals(userGroup)) {
            List<Beholdning> pensjonsbeholdningList = pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr);
            List<Inntekt> inntektList = createInntektList(fodselsdato, fnr);

            return createResponseForUserGroup5(fodselsdato, pensjonsbeholdningList, restpensjonList, inntektList, uttaksgradhistorikk, afphistorikk, uforehistorikk);
        } else if (UserGroup.USER_GROUP_4.equals(userGroup)) {
            List<Beholdning> pensjonsbeholdningList = pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr);
            List<Pensjonspoeng> pensjonspoengList = pensjonspoengConsumer.getPensjonspoengListe(fnr);

            return createResponseForUserGroup4(fodselsdato, pensjonspoengList, pensjonsbeholdningList, restpensjonList, uttaksgradhistorikk, afphistorikk, uforehistorikk);
        } else {
            List<Pensjonspoeng> pensjonspoengList = pensjonspoengConsumer.getPensjonspoengListe(fnr);

            return createResponseForUserGroups123(fodselsdato, pensjonspoengList, restpensjonList, uttaksgradhistorikk, afphistorikk, uforehistorikk);
        }
    }

    private LocalDate getFodselsdato(String fnr) {
        try {
            List<Foedsel> pdlFoedselDataList = pdlConsumer.getPdlResponse(new PdlRequest(fnr)).getData().getHentPerson().getFoedsel();
            if (pdlFoedselDataList != null && !pdlFoedselDataList.isEmpty()) {
                Foedsel foedsel = pdlFoedselDataList.get(0);
                if (foedsel.getFoedselsdato() != null) {
                    return foedsel.getFoedselsdato();
                } else if (foedsel.getFoedselsaar() != null) {
                    LOGGER.warn("No fodselsdato found for fnr in PDL, but found fodselsaar. Fodselsdato was set to first day in fodselsaar.");
                    return LocalDate.of(foedsel.getFoedselsaar(), 1, 1);
                }
            }
        } catch (FailedCallingExternalServiceException e) {
            LOGGER.error("Call to PDL failed. Deriving fodselsdato directly from fnr instead");
            return FnrUtil.getFodselsdatoForFnr(fnr);
        }
        LOGGER.warn("No fodselsdato found in PDL for fnr. Deriving fodselsdato directly from fnr instead");
        return FnrUtil.getFodselsdatoForFnr(fnr);
    }

    private Map<Integer, Beholdning> createBeholdningMap(List<Beholdning> beholdningList) {
        Map<Integer, Beholdning> beholdningMap = new HashMap<>();
        if (beholdningList != null) {
            for (Beholdning beholdning : beholdningList) {
                if (beholdning.getFomDato() != null) {
                    beholdningMap.put(beholdning.getFomDato().getYear(), beholdning);
                }
            }
        }
        return beholdningMap;
    }

    private boolean shouldGetRestpensjon(UserGroup userGroup, List<Uttaksgrad> uttaksgradhistorikk) {
        return List.of(UserGroup.USER_GROUP_2, UserGroup.USER_GROUP_3, UserGroup.USER_GROUP_4, UserGroup.USER_GROUP_5).contains(userGroup)
                && userHasUttakAlderspensjonWithUttaksgradLessThan100(uttaksgradhistorikk);
    }

    private boolean userHasUttakAlderspensjonWithUttaksgradLessThan100(List<Uttaksgrad> uttaksgradhistorikk) {
        for (Uttaksgrad uttaksgrad : uttaksgradhistorikk) {
            if (uttaksgrad.getUttaksgrad() < 100) {
                return true;
            }
        }
        return false;
    }

    private List<Inntekt> createInntektList(LocalDate fodselsdato, String fnr) {
        int firstPossibleInntektsaar = fodselsdato.getYear() + 13;
        return opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(fnr, firstPossibleInntektsaar, LocalDate.now().getYear());
    }

    private OpptjeningResponse createResponseForUserGroup5(LocalDate fodselsdato, List<Beholdning> pensjonsbeholdningList, List<Restpensjon> restpensjonListe,
            List<Inntekt> inntektsopptjeningListe, List<Uttaksgrad> uttaksgradhistorikk, AfpHistorikk afphistorikk, UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse();
        Map<Integer, OpptjeningDto> opptjeningMap = createOpptjeningMap(new ArrayList<>(), restpensjonListe);
        Map<Integer, Long> aarSumPensjonsgivendeInntektMap = createAarSumPensjonsgivendeInntektMap(inntektsopptjeningListe);

        populatePensjonsbeholdning(opptjeningMap, createBeholdningMap(pensjonsbeholdningList));
        if (opptjeningMap.isEmpty()) {
            return response;
        } else {
            int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
            int lastYearWithOpptjening = retrieveLatestOpptjeningsar(opptjeningMap);
            populateRestpensjon(opptjeningMap, restpensjonListe);
            removeFutureOpptjFromPensjonspoengMap(opptjeningMap, lastYearWithOpptjening);
            populateAdditionalInntektsaar(aarSumPensjonsgivendeInntektMap, opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            createOpptjeningYearsWithNoOpptjening(opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            populateMerknadForOpptjening(opptjeningMap, pensjonsbeholdningList, uttaksgradhistorikk, afphistorikk, uforeHistorikk);
        }

        populateEndringOpptjening(opptjeningMap, pensjonsbeholdningList);

        response.setOpptjeningData(opptjeningMap);

        return response;
    }

    private OpptjeningResponse createResponseForUserGroup4(LocalDate fodselsdato, List<Pensjonspoeng> pensjonspoengList, List<Beholdning> pensjonsbeholdningList,
            List<Restpensjon> restpensjonListe, List<Uttaksgrad> uttaksgradhistorikk, AfpHistorikk afphistorikk, UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse();
        Map<Integer, OpptjeningDto> opptjeningMap = createOpptjeningMap(pensjonspoengList, restpensjonListe);
        populatePensjonspoeng(opptjeningMap, pensjonspoengList);
        populatePensjonsbeholdning(opptjeningMap, createBeholdningMap(pensjonsbeholdningList));
        if (opptjeningMap.isEmpty()) {
            return response;
        } else {
            int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
            int lastYearWithOpptjening = retrieveLatestOpptjeningsar(opptjeningMap);
            populateRestpensjon(opptjeningMap, restpensjonListe);
            removeFutureOpptjFromPensjonspoengMap(opptjeningMap, lastYearWithOpptjening);
            createOpptjeningYearsWithNoOpptjening(opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            populateMerknadForOpptjening(opptjeningMap, pensjonsbeholdningList, uttaksgradhistorikk, afphistorikk, uforeHistorikk);
        }

        response.setNumberOfYearsWithPensjonspoeng(findNumberOfYearsWithPensjonspoeng(opptjeningMap));

        populateEndringOpptjening(opptjeningMap, pensjonsbeholdningList);

        response.setOpptjeningData(opptjeningMap);

        return response;
    }

    private OpptjeningResponse createResponseForUserGroups123(LocalDate fodselsdato, List<Pensjonspoeng> pensjonspoengList, List<Restpensjon> restpensjonListe,
            List<Uttaksgrad> uttaksgradhistorikk, AfpHistorikk afphistorikk, UforeHistorikk uforeHistorikk) {
        OpptjeningResponse response = new OpptjeningResponse();
        Map<Integer, OpptjeningDto> opptjeningMap = createOpptjeningMap(pensjonspoengList, restpensjonListe);
        populatePensjonspoeng(opptjeningMap, pensjonspoengList);

        if (opptjeningMap.isEmpty()) {
            return response;
        } else {
            int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
            int lastYearWithOpptjening = retrieveLatestOpptjeningsar(opptjeningMap);
            populateRestpensjon(opptjeningMap, restpensjonListe);
            removeFutureOpptjFromPensjonspoengMap(opptjeningMap, lastYearWithOpptjening);
            createOpptjeningYearsWithNoOpptjening(opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            populateMerknadForOpptjening(opptjeningMap, null, uttaksgradhistorikk, afphistorikk, uforeHistorikk);
        }

        response.setNumberOfYearsWithPensjonspoeng(findNumberOfYearsWithPensjonspoeng(opptjeningMap));
        response.setOpptjeningData(opptjeningMap);

        return response;
    }

    private Map<Integer, OpptjeningDto> createOpptjeningMap(List<Pensjonspoeng> pensjonspoengList, List<Restpensjon> restpensjonList) {
        Map<Integer, OpptjeningDto> opptjeningMap = new HashMap<>();

        pensjonspoengList.forEach(pensjonspoeng -> {
            Integer year = pensjonspoeng.getAr();
            opptjeningMap.put(year, createOpptjening(opptjeningMap, year));
        });

        restpensjonList.forEach(restpensjon -> {
            int year = restpensjon.getFomDato().getYear();
            opptjeningMap.put(year, createOpptjening(opptjeningMap, year));
        });

        return opptjeningMap;
    }

    private OpptjeningDto createOpptjening(Map<Integer, OpptjeningDto> opptjeningMap, int ar) {
        OpptjeningDto opptjening;
        if (opptjeningMap.containsKey(ar)) {
            opptjening = opptjeningMap.get(ar);
        } else {
            opptjening = new OpptjeningDto();
        }

        return opptjening;
    }

    private void populatePensjonsbeholdning(Map<Integer, OpptjeningDto> opptjeningMap, Map<Integer, Beholdning> pensjonsbeholdningMap) {
        for (Map.Entry<Integer, Beholdning> entry : pensjonsbeholdningMap.entrySet()) {
            Integer year = entry.getKey();
            Beholdning beholdning = entry.getValue();
            OpptjeningDto opptjening = createOpptjening(opptjeningMap, year);
            opptjening.setPensjonsbeholdning(Math.round(beholdning.getBelop()));
            if (!opptjeningMap.containsKey(year)) {
                opptjeningMap.put(year, opptjening);
            }
        }
        addInntektToCorrespondingYear(opptjeningMap, pensjonsbeholdningMap);
    }

    private void addInntektToCorrespondingYear(Map<Integer, OpptjeningDto> opptjeningMap,
            Map<Integer, Beholdning> pensjonsbeholdningMap) {
        for (Beholdning beholdning : pensjonsbeholdningMap.values()) {
            if (beholdning.getInntektOpptjeningBelop() != null) {
                Integer ar = beholdning.getInntektOpptjeningBelop().getAr();
                OpptjeningDto opptjening = opptjeningMap.get(ar);
                if (opptjening != null && beholdning.getInntektOpptjeningBelop().getSumPensjonsgivendeInntekt() != null) {
                    opptjening.setPensjonsgivendeInntekt(beholdning.getInntektOpptjeningBelop().getSumPensjonsgivendeInntekt().getBelop().intValue());
                }
            }
        }
    }

    private Map<Integer, Long> createAarSumPensjonsgivendeInntektMap(List<Inntekt> inntektsopptjeningListe) {
        return inntektsopptjeningListe.stream()
                .filter(inntektsopptjening -> inntektsopptjening.getInntektType().equals("SUM_PI"))
                .collect(Collectors.toMap(Inntekt::getInntektAr, Inntekt::getBelop));
    }

    private void populateRestpensjon(Map<Integer, OpptjeningDto> opptjeningMap,
            List<Restpensjon> restpensjonListe) {
        for (Restpensjon restpensjon : restpensjonListe) {
            OpptjeningDto opptjening = opptjeningMap.get(restpensjon.getFomDato().getYear());
            Double belop = calculateBelopFromRestpensjon(restpensjon);
            if (opptjening != null) {
                opptjening.setRestpensjon(belop);
            }
        }
    }

    private double calculateBelopFromRestpensjon(Restpensjon restpensjon) {
        double resultat = 0.0;
        if (restpensjon.getRestGrunnpensjon() != null) {
            resultat += restpensjon.getRestGrunnpensjon();
        }
        if (restpensjon.getRestTilleggspensjon() != null) {
            resultat += restpensjon.getRestTilleggspensjon();
        }
        if (restpensjon.getRestPensjonstillegg() != null && restpensjon.getRestPensjonstillegg() > 0) {
            resultat += restpensjon.getRestPensjonstillegg();
        }
        return resultat;
    }

    private int getFirstYearWithOpptjening(LocalDate fodselsdato) {
        LocalDate firstDayOf1967 = LocalDate.of(1967, Month.JANUARY, 1);
        LocalDate firstDayOf1993 = LocalDate.of(1993, Month.JANUARY, 1);
        LocalDate firstDayOf1994 = LocalDate.of(1994, Month.JANUARY, 1);
        LocalDate firstDayOf1995 = LocalDate.of(1995, Month.JANUARY, 1);
        LocalDate firstDayOf1996 = LocalDate.of(1996, Month.JANUARY, 1);

        LocalDate firstYear = fodselsdato.plusYears(17);

        if (firstYear.isBefore(firstDayOf1967)) {
            return 1967;
        } else if (fodselsdato.isBefore(firstDayOf1993)) {
            return fodselsdato.getYear() + 17;
        } else if (fodselsdato.isBefore(firstDayOf1994)) {
            return fodselsdato.getYear() + 16;
        } else if (fodselsdato.isBefore(firstDayOf1995)) {
            return fodselsdato.getYear() + 15;
        } else if (fodselsdato.isBefore(firstDayOf1996)) {
            return fodselsdato.getYear() + 14;
        } else {
            return fodselsdato.getYear() + 13;
        }
    }

    private void removeFutureOpptjFromPensjonspoengMap(Map<Integer, OpptjeningDto> opptjeningMap, int latestOpptjeningsar) {
        opptjeningMap.entrySet().stream()
                .filter(opptjeningEntry -> opptjeningEntry.getKey() > latestOpptjeningsar)
                .forEach(opptjeningEntry -> opptjeningMap.remove(opptjeningEntry.getKey()));
    }

    private int retrieveLatestOpptjeningsar(Map<Integer, OpptjeningDto> pensjonspoengMap) {
        int lastYearWithOpptjening = -1;
        int thisYear = LocalDate.now().getYear();

        for (Integer opptjeningAr : pensjonspoengMap.keySet()) {
            if (opptjeningAr > lastYearWithOpptjening && opptjeningAr <= thisYear) {
                lastYearWithOpptjening = opptjeningAr;
            }
        }
        return lastYearWithOpptjening > -1 ? lastYearWithOpptjening : thisYear;
    }

    /**
     * Method for adding opptjening to opptjeningMap for years with inntekt but no beholdning
     */
    private void populateAdditionalInntektsaar(Map<Integer, Long> aarInntektMap,
            Map<Integer, OpptjeningDto> opptjeningMap, int firstYearWithOpptjening, int lastYearWithOpptjening) {
        for (int year = firstYearWithOpptjening; year <= lastYearWithOpptjening; year++) {
            if (!opptjeningMap.containsKey(year) && aarInntektMap.containsKey(year)) {
                OpptjeningDto opptjening = createOpptjening(opptjeningMap, year);
                opptjening.setPensjonsgivendeInntekt(aarInntektMap.get(year).intValue());
                opptjeningMap.put(year, opptjening);
            }
        }
    }

    private void createOpptjeningYearsWithNoOpptjening(Map<Integer, OpptjeningDto> pensjonspoengMap, int firstYearWithOpptjening, int lastYearWithOpptjening) {
        if (firstYearWithOpptjening > 0 && lastYearWithOpptjening > 0) {
            for (int year = firstYearWithOpptjening; year <= lastYearWithOpptjening; year++) {
                if (!pensjonspoengMap.containsKey(year)) {
                    OpptjeningDto opptjening = new OpptjeningDto();
                    opptjening.setPensjonsgivendeInntekt(0);
                    opptjening.setPensjonspoeng(0.0);
                    pensjonspoengMap.put(year, opptjening);
                }
            }
        }
    }

    private void populatePensjonspoeng(Map<Integer, OpptjeningDto> opptjeningMap, List<Pensjonspoeng> pensjonspoengList) {
        pensjonspoengList.stream().filter(pensjonspoeng -> pensjonspoeng.getAr() != null).forEach(pensjonspoeng -> {
            Integer arOpptjening = pensjonspoeng.getAr();
            OpptjeningDto opptjening = opptjeningMap.get(arOpptjening);
            populateOpptjeningMapWithPensjonspoeng(opptjening, pensjonspoeng);
        });
    }

    private void populateOpptjeningMapWithPensjonspoeng(OpptjeningDto opptjening,
            Pensjonspoeng pensjonspoeng) {
        if (isOpptjeningTypeInntekt(pensjonspoeng.getPensjonspoengType())) {
            populatePensjonspoengInntekt(pensjonspoeng, opptjening);
        }

        if (isOpptjeningTypeOmsorgspoeng(pensjonspoeng.getPensjonspoengType())) {
            populateOmsorgspoeng(pensjonspoeng, opptjening);
            merknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng);
        }

        if (isOmsorgspoengGreaterThanPensjonspoeng(opptjening)) {
            opptjening.setPensjonspoeng(opptjening.getOmsorgspoeng());
        }

        merknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng);
    }

    private boolean isOpptjeningTypeInntekt(String opptjeningType) {
        return OpptjeningTypeCode.PPI.toString().equals(opptjeningType);
    }

    private boolean isOpptjeningTypeOmsorgspoeng(String opptjeningType) {
        return List.of(OpptjeningTypeCode.OBO7H.toString(), OpptjeningTypeCode.OBU7.toString(),
                OpptjeningTypeCode.OSFE.toString(), OpptjeningTypeCode.OBO6H.toString(), OpptjeningTypeCode.OBU6.toString()).contains(opptjeningType);
    }

    private boolean isOmsorgspoengGreaterThanPensjonspoeng(OpptjeningDto opptjening) {
        boolean pensjonspoengIsNullButOmsorgspoengIsNotNull = opptjening.getOmsorgspoeng() != null
                && opptjening.getPensjonspoeng() == null;
        boolean bothOmsorgspoengAndPensjonspoengIsNotNull = opptjening.getOmsorgspoeng() != null
                && opptjening.getPensjonspoeng() != null;
        return pensjonspoengIsNullButOmsorgspoengIsNotNull
                || bothOmsorgspoengAndPensjonspoengIsNotNull
                && opptjening.getPensjonspoeng() < opptjening.getOmsorgspoeng();
    }

    private void populateOmsorgspoeng(Pensjonspoeng pensjonspoeng, OpptjeningDto opptjening) {
        opptjening.setOmsorgspoeng(pensjonspoeng.getPoeng());
        opptjening.setOmsorgspoengType(pensjonspoeng.getPensjonspoengType());
    }

    private void populatePensjonspoengInntekt(Pensjonspoeng pensjonspoeng, OpptjeningDto opptjening) {
        Double poeng = pensjonspoeng.getPoeng();
        opptjening.setPensjonspoeng(poeng);
        opptjening.setPensjonsgivendeInntekt(pensjonspoeng.getInntekt().getBelop().intValue());
    }

    private int findNumberOfYearsWithPensjonspoeng(Map<Integer, OpptjeningDto> opptjeningMap) {

        int numberOfYearsWithPensjonspoeng = 0;

        for (OpptjeningDto opptjening : opptjeningMap.values()) {
            if (opptjening.getPensjonspoeng() != null && opptjening.getPensjonspoeng() > 0) {
                numberOfYearsWithPensjonspoeng++;
            }
        }
        return numberOfYearsWithPensjonspoeng;
    }

    private void populateEndringOpptjening(Map<Integer, OpptjeningDto> opptjeningMap, List<Beholdning> beholdningList) {
        List<Long> vedtakIdForBeholdningAfter2009 = beholdningList.stream()
                .filter(beholdning -> beholdning.getVedtakId() != null && beholdning.getFomDato().getYear() >= REFORM_2010 - 1)
                .map(Beholdning::getVedtakId).collect(Collectors.toList());

        List<Uttaksgrad> uttaksgradForBeholdningAfter2009 = uttaksgradConsumer.getUttaksgradForVedtak(vedtakIdForBeholdningAfter2009);

        opptjeningMap.entrySet().stream()
                .filter(entry -> entry.getKey() >= REFORM_2010)
                .forEach(entry -> entry.getValue().setEndringOpptjening(
                        endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(entry.getKey(), beholdningList, uttaksgradForBeholdningAfter2009)));
    }

    private void populateMerknadForOpptjening(Map<Integer, OpptjeningDto> opptjeningMap, List<Beholdning> pensjonsbeholdningList, List<Uttaksgrad> uttaksgradhistorikk,
            AfpHistorikk afphistorikk, UforeHistorikk uforehistorikk) {
        opptjeningMap.forEach((key, value) -> merknadHandler.addMerknaderOnOpptjening(key, value, pensjonsbeholdningList, uttaksgradhistorikk, afphistorikk, uforehistorikk));
    }

    @Autowired
    public void setPensjonsbeholdningConsumer(PensjonsbeholdningConsumer pensjonsbeholdningConsumer) {
        this.pensjonsbeholdningConsumer = pensjonsbeholdningConsumer;
    }

    @Autowired
    public void setOpptjeningsgrunnlagConsumer(OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer) {
        this.opptjeningsgrunnlagConsumer = opptjeningsgrunnlagConsumer;
    }

    @Autowired
    public void setPensjonspoengConsumer(PensjonspoengConsumer pensjonspoengConsumer) {
        this.pensjonspoengConsumer = pensjonspoengConsumer;
    }

    @Autowired
    public void setRestpensjonConsumer(RestpensjonConsumer restpensjonConsumer) {
        this.restpensjonConsumer = restpensjonConsumer;
    }

    @Autowired
    public void setPersonConsumer(PersonConsumer personConsumer) {
        this.personConsumer = personConsumer;
    }

    @Autowired
    public void setUttaksgradConsumer(UttaksgradConsumer uttaksgradConsumer) {
        this.uttaksgradConsumer = uttaksgradConsumer;
    }

    @Autowired
    public void setPdlConsumer(PdlConsumer pdlConsumer) {
        this.pdlConsumer = pdlConsumer;
    }

    @Autowired
    public void setEndringPensjonsbeholdningCalculator(EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator) {
        this.endringPensjonsbeholdningCalculator = endringPensjonsbeholdningCalculator;
    }

    @Autowired
    public void setMerknadHandler(MerknadHandler merknadHandler) {
        this.merknadHandler = merknadHandler;
    }
}
