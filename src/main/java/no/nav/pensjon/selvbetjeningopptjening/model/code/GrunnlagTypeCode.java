package no.nav.pensjon.selvbetjeningopptjening.model.code;

public enum GrunnlagTypeCode {
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
     * Finnes ikke noe opptjeningsgrunnlag for dette året
     */
    NO_GRUNNLAG
}
