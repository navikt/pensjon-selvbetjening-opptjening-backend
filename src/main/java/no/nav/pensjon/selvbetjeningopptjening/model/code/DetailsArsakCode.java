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
     * Pensjonsbeholdningen reguleres årlig i samsvar med lønnsveksten (&lt;a href="https://lovdata.no/nav/folketrygdloven/kap20/%C2%A720-18" target="_blank" title="åpne § 20-18
     * i folketrygdloven"&gt;se § 20-18 i folketrygdloven &lt;img src="/pselv/images/nytt_vindu.gif" alt="åpnes i nytt vindu" title="åpne § 20-18 folketrygdloven"/&gt;&lt;/a&gt;
     * ). (FOTNOTE_REGULERING)
     */
    REGULERING,

    /**
     * Ved uttak reduseres pensjonsbeholdningen din med like stor andel som uttaksgraden du har valgt. (FOTNOTE_UTTAK)
     */
    UTTAK,

    /**
     * Pensjonsopptjeningen for et kalenderår oppreguleres med lønnsvekst og tilføres pensjonsbeholdningen ved utløpet av året ligningen for det aktuelle året er ferdig (&lt;a
     * href="https://lovdata.no/nav/folketrygdloven/kap20" target="_blank" title="åpne § 20-4 i
     * folketrygdloven"&gt;se § 20-4 i folketrygdloven &lt;img src="/pselv/images/nytt_vindu.gif" alt="åpnes i nytt vindu" title="åpne § 20-4 folketrygdloven"/&gt;&lt;/a&gt;)
     * .(FOTNOTE_OPPTJENING_2012)
     */
    OPPTJENING_2012,

    /**
     * Fram til 1. mai 2011 er det ikke fastsatt en egen lønnsvekstfaktor. Endringen i folketrygdens grunnbeløp denne årlige lønnsveksten. Beholdningen er i 2010 derfor regulert
     * med
     * forholdet mellom folketrygdens grunnbeløp 1. mai 2010 (75 641) og grunnbeløpet 1. januar 2010 (72 881).(FOTNOTE_REGULERING_2010)
     */
    REGULERING_2010,

    /**
     * Pensjonsopptjeningen for 2009 oppreguleres med grunnbeløpet på beregningstidspunktet (75 641) og gjennomsnittlig grunnbeløp for 2009 ( &lt;a href="https://lovdata
     * .no/nav/folketrygdloven/kap20/%C2%A720-21" target="_blank" title="åpne § 20-21 i folketrygdloven"&gt;se tilhørende forskrift til § 20-21 i folketrygdloven &lt;img
     * src="/pselv/images/nytt_vindu.gif" alt="åpnes i nytt vindu" title="åpne § 20-11 folketrygdloven"/&gt;&lt;/a&gt; ).(FOTNOTE_OPPTJENING_2011)
     */
    OPPTJENING_2011;
}
