package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OpptjeningProvider {

    public OpptjeningResponse calculateOpptjeningForFnr(String fnr) {
        /*
               OpptjeningResponse response
            1: pen.hentPerson(fnr) (Trenger kun: Fødselsdato, AFPHistorikk, UføreHistorikk)
            2: HVIS isUserGroup2() || isUserGroup3() || isUserGroup4() || isUserGroup5():
                    sakliste = pen.hentSakliste(fnr, alder) (Trenger kun å vite om det finnes saker, og uttaksgradhistorikk på hver sak)
                    HVIS det finnes en sak i saklista som har uttaksgrad < 100 OG (isUserGroup2() || isUserGroup3() || isUserGroup4()):
                        restpensjonliste = popp.hentRestPensjonListe(fnr)

            3: HVIS isUserGroup5:
                    pensjonsbeholdningMap = popp.hentPensjonsbeholdningMap(fnr)
                    inntektListe = popp.hentInntekt(fnr)

                    POPULER respons basert på pensjonsbeholdningMap, restpensjonliste, sakliste og inntektListe

               ELLER HVIS isUserGroup4:
                    pensjonsbeholdningMap = popp.hentPensjonsbeholdningMap(fnr)
                    pensjonspoengListe = popp.hentPensjonspoengListe(fnr)

                    POPULER respons basert på pensjonsbeholdningMap, restpensjonliste, sakliste og pensjonspoengListe
               ELLERS:
                    pensjonspoengListe = popp.hentPensjonspoengListe(fnr)

                    POPULER respons basert på restpensjonliste, sakliste og pensjonspoengListe



        */
        return createDummyResponse();
    }

    private OpptjeningResponse createDummyResponse() {
        Random random = new Random();
        int firstYear = 1970;
        int lastYear = 2015;
        OpptjeningResponse response = new OpptjeningResponse();
        response.setFirstYearWithOpptjening(firstYear);
        response.setLastYearWithOpptjening(lastYear);
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
}
