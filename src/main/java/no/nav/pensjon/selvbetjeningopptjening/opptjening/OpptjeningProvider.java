package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class OpptjeningProvider {

    public OpptjeningResponse calculateOpptjeningForFnr(String fnr){
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
        return new OpptjeningResponse();
    }
}
