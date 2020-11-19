package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlagDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HentOpptjeningsGrunnlagResponse {

    private OpptjeningsGrunnlagDto opptjeningsGrunnlag;

    public OpptjeningsGrunnlagDto getOpptjeningsGrunnlag() {
        return opptjeningsGrunnlag;
    }

    public void setOpptjeningsGrunnlag(OpptjeningsGrunnlagDto opptjeningsGrunnlag) {
        this.opptjeningsGrunnlag = opptjeningsGrunnlag;
    }
}
