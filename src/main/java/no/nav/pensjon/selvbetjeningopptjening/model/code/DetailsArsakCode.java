package no.nav.pensjon.selvbetjeningopptjening.model.code;

public enum DetailsArsakCode {
    /**
     * Pensjonsbeholdningen din ble etablert med virkning 1. januar 2010 i forbindelse med at pensjonsreformen trådte i kraft. Da ble den opptjeningen du hadde i kalenderår frem
     * til og med 2008 (siste ferdiglignede år) summert til beholdningsstørrelse. (FOTNOTE_BEHOLDNING_2010)
     */
    BEHOLDNING_2010,

    /**
     * Fordi du har gradert uttak har den nye opptjeningen blitt lagt til pensjonsbeholdningen din.(FOTNOTE_OPPTJENING_GRADERT)
     */
    OPPTJENING_GRADERT,

    /**
     * Fordi du tar ut hel alderspensjon (100 prosent) har ny opptjening ført til en økning i den utbetalte pensjonen din før skatt.(FOTNOTE_OPPTJENING_HEL)
     */
    OPPTJENING_HEL,

    /**
     * Pensjonsbeholdningen reguleres årlig i samsvar med lønnsveksten. (FOTNOTE_REGULERING)
     */
    REGULERING,

    /**
     * Ved uttak reduseres pensjonsbeholdningen din med like stor andel som uttaksgraden du har valgt. (FOTNOTE_UTTAK)
     */
    UTTAK,

    /**
     * Pensjonsopptjeningen for et kalenderår oppreguleres med lønnsvekst og tilføres pensjonsbeholdningen ved utløpet av året ligningen for det aktuelle året er ferdig.
     * (FOTNOTE_OPPTJENING_2012)
     */
    OPPTJENING_2012,

    /**
     * Fram til 1. mai 2011 er det ikke fastsatt en egen lønnsvekstfaktor. Endringen i folketrygdens grunnbeløp denne årlige lønnsveksten. Beholdningen er i 2010 derfor regulert
     * med forholdet mellom folketrygdens grunnbeløp 1. mai 2010 (75 641) og grunnbeløpet 1. januar 2010 (72 881).(FOTNOTE_REGULERING_2010)
     */
    REGULERING_2010,

    /**
     * Pensjonsopptjeningen for 2009 oppreguleres med grunnbeløpet på beregningstidspunktet (75 641) og gjennomsnittlig grunnbeløp for 2009..(FOTNOTE_OPPTJENING_2011)
     */
    OPPTJENING_2011,

    /**
     * Grunnlaget som beholdningsendringen er regnet ut fra er lønnsinntekt.
     */
    INNTEKT_GRUNNLAG,

    /**
     * Grunnlaget som beholdningsendringen er regnet ut fra er antatt inntekt i forbindelse med uføretrygd.
     */
    UFORE_GRUNNLAG,

    /**
     * Bruker hadde gradert uføretrygd. Grunnlaget for beholdningsendringen avgjøres dermed ut fra både det man fikk i uføretrygd, men også eventuelle inntekter og andre ytelser
     * man hadde det året.
     */
    GRADERT_UFORE_GRUNNLAG,

    /**
     * Grunnlaget som beholdningsendringen er regnet ut fra er 2,5 ganger G, som er standard grunnlag ved førstegangstjeneste.
     */
    FORSTEGANGSTJENESTE_GRUNNLAG,

    /**
     * Grunnlaget som beholdningsendringen er regnet ut fra er den inntekten dagpengene er satt ut fra.
     */
    DAGPENGER_GRUNNLAG,

    /**
     * Grunnlaget som beholdningsendringen er regnet ut fra er X ganger grunnbeløpet, som er standard grunnlag i tilfeller hvor inntekt man har samtidig med omsorgsopptjening er
     * lavere
     * enn X ganger grunnbeløpet. (X kan variere fra år til år)
     */
    OMSORGSOPPTJENING_GRUNNLAG,

    /**
     * Grunnlaget som beholdningsendringen er regnet ut fra matcher ingen enkeltstående ytelse/inntekt brukeren har hatt og antas derfor å være en kombinasjon av flere ytelser
     * og inntekt bruker har hatt.
     */
    KOMBINERT_GRUNNLAG,

    /**
     * Det var ikke mulig å konstatere hva slags grunnlag opptjeningen skyldtes. Dette fordi beløpet som utgjør grunnlaget for beholdningen forekommer på flere typer
     * opptjening, eller grunnlagsinformasjon mangler.
     */
    UNDETERMINED_GRUNNLAG
}
