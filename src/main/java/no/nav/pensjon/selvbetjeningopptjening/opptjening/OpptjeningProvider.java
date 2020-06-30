package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sak.SakConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.OpptjeningTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup;
import no.nav.pensjon.selvbetjeningopptjening.util.UserGroupUtil;

public class OpptjeningProvider {
    private PensjonsbeholdningConsumer pensjonsbeholdningConsumer;
    private OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer;
    private PensjonspoengConsumer pensjonspoengConsumer;
    private RestpensjonConsumer restpensjonConsumer;
    private PersonConsumer personConsumer;
    private SakConsumer sakConsumer;

    public OpptjeningResponse returnDummyResponse(String fnr) {
        return createDummyResponse();
    }

    public OpptjeningResponse calculateOpptjeningForFnr(String fnr) {
        LocalDate fodselsdato = personConsumer.getFodselsdato(fnr);
        UserGroup userGroup = UserGroupUtil.findUserGroup(fodselsdato);
        List<Restpensjon> restpensjonList = new ArrayList<>();

        if (shouldGetRestpensjon(userGroup, fnr)) {
            restpensjonList = restpensjonConsumer.getRestpensjonListe(fnr);
        }

        if (UserGroup.USER_GROUP_5.equals(userGroup)) {
            Map<Integer, Beholdning> pensjonsbeholdningMap = pensjonsbeholdningConsumer.getPensjonsbeholdningMap(fnr);
            List<Inntekt> inntektList = createInntektList(fodselsdato, fnr);

            return createResponseForUserGroup5(fodselsdato, pensjonsbeholdningMap, restpensjonList, inntektList);
        } else if (UserGroup.USER_GROUP_4.equals(userGroup)) {
            Map<Integer, Beholdning> pensjonsbeholdningMap = pensjonsbeholdningConsumer.getPensjonsbeholdningMap(fnr);
            List<Pensjonspoeng> pensjonspoengList = pensjonspoengConsumer.getPensjonspoengListe(fnr);

            return createResponseForUserGroup4(fodselsdato, pensjonspoengList, pensjonsbeholdningMap, restpensjonList);
        } else {
            List<Pensjonspoeng> pensjonspoengList = pensjonspoengConsumer.getPensjonspoengListe(fnr);

            return createResponseForUserGroups123(fodselsdato, pensjonspoengList, restpensjonList);
        }
    }

    private boolean shouldGetRestpensjon(UserGroup userGroup, String fnr) {
        List<String> sakList = personConsumer.getSakIdListForPerson(fnr);
        return List.of(UserGroup.USER_GROUP_2, UserGroup.USER_GROUP_3, UserGroup.USER_GROUP_4, UserGroup.USER_GROUP_5).contains(userGroup)
                && userHasUttakAlderspensjonWithUttaksgradLessThan100(sakList);
    }

    //TODO: Vurder om dette er en optimal løsning, blir ett kall pr. sak. Vurder å heller ha tjeneste som returnerer uttaksgrad sammen med sakid og saktype el.
    private boolean userHasUttakAlderspensjonWithUttaksgradLessThan100(List<String> sakList) {
        for (String sakId : sakList) {
            List<Uttaksgrad> uttaksgradHistorikkForSak = sakConsumer.getUttaksgradhistorikkForSak(sakId);
            for (Uttaksgrad uttaksgrad : uttaksgradHistorikkForSak) {
                if (uttaksgrad.getUttaksgrad() < 100) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Inntekt> createInntektList(LocalDate fodselsdato, String fnr) {
        int firstPossibleInntektsaar = fodselsdato.getYear() + 13;
        return opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(fnr, firstPossibleInntektsaar, LocalDate.now().getYear());
    }

    public OpptjeningResponse createResponseForUserGroup5(LocalDate fodselsdato, Map<Integer, Beholdning> pensjonsbeholdningMap, List<Restpensjon> restpensjonListe,
            List<Inntekt> inntektsopptjeningListe) {
        OpptjeningResponse response = new OpptjeningResponse();
        Map<Integer, OpptjeningDto> opptjeningMap = createOpptjeningMap(new ArrayList<>(), restpensjonListe);
        Map<Integer, Long> aarSumPensjonsgivendeInntektMap = createAarSumPensjonsgivendeInntektMap(inntektsopptjeningListe);

        populatePensjonsbeholdning(opptjeningMap, pensjonsbeholdningMap);
        if (opptjeningMap.isEmpty()) {
            return response;
        } else {
            int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
            int lastYearWithOpptjening = retrieveLatestOpptjeningsar(opptjeningMap);
            populateRestpensjon(opptjeningMap, restpensjonListe);
            removeFutureOpptjFromPensjonspoengMap(opptjeningMap, lastYearWithOpptjening);
            populateAdditionalInntektsaar(aarSumPensjonsgivendeInntektMap, opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            createOpptjeningYearsWithNoOpptjening(opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            //TODO: Implement population of merknader
        }

        response.setOpptjeningData(opptjeningMap);

        return response;
    }

    public OpptjeningResponse createResponseForUserGroup4(LocalDate fodselsdato, List<Pensjonspoeng> pensjonspoengList, Map<Integer, Beholdning> pensjonsbeholdningMap,
            List<Restpensjon> restpensjonListe) {
        OpptjeningResponse response = new OpptjeningResponse();
        Map<Integer, OpptjeningDto> opptjeningMap = createOpptjeningMap(pensjonspoengList, restpensjonListe);
        populatePensjonspoeng(opptjeningMap, pensjonspoengList);
        populatePensjonsbeholdning(opptjeningMap, pensjonsbeholdningMap);
        if (opptjeningMap.isEmpty()) {
            return response;
        } else {
            int firstYearWithOpptjening = getFirstYearWithOpptjening(fodselsdato);
            int lastYearWithOpptjening = retrieveLatestOpptjeningsar(opptjeningMap);
            populateRestpensjon(opptjeningMap, restpensjonListe);
            removeFutureOpptjFromPensjonspoengMap(opptjeningMap, lastYearWithOpptjening);
            createOpptjeningYearsWithNoOpptjening(opptjeningMap, firstYearWithOpptjening, lastYearWithOpptjening);
            //TODO: Implement population of merknader
        }

        response.setNumberOfYearsWithPensjonspoeng(findNumberOfYearsWithPensjonspoeng(opptjeningMap));

        response.setOpptjeningData(opptjeningMap);

        return response;
    }

    public OpptjeningResponse createResponseForUserGroups123(LocalDate fodselsdato, List<Pensjonspoeng> pensjonspoengList, List<Restpensjon> restpensjonListe) {
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
            //TODO: Implement population of merknader
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

    private OpptjeningDto createOpptjening(
            Map<Integer, OpptjeningDto> pensjonspoengMap, int ar) {
        OpptjeningDto pensjonspoeng;
        if (pensjonspoengMap.containsKey(ar)) {
            pensjonspoeng = pensjonspoengMap.get(ar);
        } else {
            pensjonspoeng = new OpptjeningDto();
            pensjonspoeng.setAr(ar);
        }
        return pensjonspoeng;
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
                .filter(inntektsopptjening -> inntektsopptjening.getInntektType().equals("SUM_PI")) //TODO: Vurder om enum skal innføres slik som i pesys
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

    private void removeFutureOpptjFromPensjonspoengMap(Map<Integer, OpptjeningDto> pensjonspoengMap, int latestOpptjeningsar) {
        pensjonspoengMap.entrySet().stream()
                .filter(opptjening -> opptjening.getValue().getAr() > latestOpptjeningsar)
                .forEach(opptjening -> pensjonspoengMap.remove(opptjening.getKey()));
    }

    private int retrieveLatestOpptjeningsar(Map<Integer, OpptjeningDto> pensjonspoengMap) {
        int lastYearWithOpptjening = -1;
        int thisYear = LocalDate.now().getYear();

        for (OpptjeningDto opptjening : pensjonspoengMap.values()) {
            if (opptjening.getAr() > lastYearWithOpptjening && opptjening.getAr() <= thisYear) {
                lastYearWithOpptjening = opptjening.getAr();
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
                    OpptjeningDto pensjonspoeng = new OpptjeningDto();
                    pensjonspoeng.setAr(year);
                    pensjonspoeng.setPensjonsgivendeInntekt(0);
                    pensjonspoeng.setPensjonspoeng(0.0);
                    pensjonspoengMap.put(year, pensjonspoeng);
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
        }

        if (isOmsorgspoengGreaterThanPensjonspoeng(opptjening)) {
            opptjening.setPensjonspoeng(opptjening.getOmsorgspoeng());
        }

        //TODO: Implement merknad for omsorg here
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
        opptjening.setRegistrertePensjonspoeng(poeng);
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

    private OpptjeningResponse createDummyResponse() {
        Random random = new Random();
        int firstYear = 1970;
        int lastYear = 2015;
        OpptjeningResponse response = new OpptjeningResponse();
        response.setNumberOfYearsWithPensjonspoeng(lastYear - firstYear);
        response.setOverforOmsorgspoengPossible(true);
        Map<Integer, OpptjeningDto> opptjeningMap = new HashMap<>();
        long pensjonsbeholdning = 0L;
        for (int year = firstYear; year <= lastYear; year++) {
            pensjonsbeholdning += random.nextInt(100000);
            opptjeningMap.put(year, createDummyOpptjening(year,
                    random.nextInt(900000),
                    (double) random.nextInt(500000),
                    random.nextDouble() * 100000,
                    random.nextInt(100),
                    random.nextDouble(),
                    pensjonsbeholdning,
                    random.nextDouble(),
                    random.nextDouble()));
        }
        response.setOpptjeningData(opptjeningMap);
        return response;
    }

    private OpptjeningDto createDummyOpptjening(int ar, int inntekt, Double restpensjon, double gjennomsnittligG, Integer maksUforegrad, Double omsorgspoeng,
            Long pensjonsbeholdning, Double pensjonspoeng, Double registrertePensjonspoeng) {
        OpptjeningDto opptjening = new OpptjeningDto();
        opptjening.setAr(ar);
        opptjening.setPensjonsgivendeInntekt(inntekt);
        opptjening.setRestpensjon(restpensjon);
        opptjening.setGjennomsnittligG(gjennomsnittligG);
        opptjening.setHjelpMerknad("En hjelp-merknad, usikker på om denne skal brukes");
        opptjening.setMaksUforegrad(maksUforegrad);
        opptjening.setOmsorgspoeng(omsorgspoeng);
        opptjening.setOmsorgspoengType("Omsorgstype");
        opptjening.setPensjonsbeholdning(pensjonsbeholdning);
        opptjening.setPensjonspoeng(pensjonspoeng);
        opptjening.setRegistrertePensjonspoeng(registrertePensjonspoeng);
        OpptjeningPensjonspoengMerknadDto opptjeningPensjonspoengMerknadDto = new OpptjeningPensjonspoengMerknadDto();
        opptjeningPensjonspoengMerknadDto.setMerknad("Et objekt som holder data om en merknad knyttet til pensjonpoeng. Gjenstår å finne ut hvordan dette skal implementeres");
        opptjening.setMerknad(Collections.singletonList(opptjeningPensjonspoengMerknadDto));
        return opptjening;
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
    public void setSakConsumer(SakConsumer sakConsumer) {
        this.sakConsumer = sakConsumer;
    }
}
