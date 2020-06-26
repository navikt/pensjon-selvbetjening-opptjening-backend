package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlag;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HentOpptjeningsGrunnlagResponse {

    private OpptjeningsGrunnlag opptjeningsGrunnlag;

    public OpptjeningsGrunnlag getOpptjeningsGrunnlag() {
        return opptjeningsGrunnlag;
    }

    public void setOpptjeningsGrunnlag(OpptjeningsGrunnlag opptjeningsGrunnlag) {
        this.opptjeningsGrunnlag = opptjeningsGrunnlag;
    }
}
