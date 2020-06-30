package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.Map;

public class OpptjeningResponse {
    private Map<Integer, OpptjeningDto> opptjeningData;

    private Integer numberOfYearsWithPensjonspoeng;

    private Boolean isOverforOmsorgspoengPossible;

    private Boolean showRestpensjon = false;

    public Map<Integer, OpptjeningDto> getOpptjeningData() {
        return opptjeningData;
    }

    public void setOpptjeningData(Map<Integer, OpptjeningDto> opptjeningData) {
        this.opptjeningData = opptjeningData;
    }

    public Integer getNumberOfYearsWithPensjonspoeng() {
        return numberOfYearsWithPensjonspoeng;
    }

    public void setNumberOfYearsWithPensjonspoeng(Integer numberOfYearsWithPensjonspoeng) {
        this.numberOfYearsWithPensjonspoeng = numberOfYearsWithPensjonspoeng;
    }

    public Boolean getOverforOmsorgspoengPossible() {
        return isOverforOmsorgspoengPossible;
    }

    public void setOverforOmsorgspoengPossible(Boolean overforOmsorgspoengPossible) {
        isOverforOmsorgspoengPossible = overforOmsorgspoengPossible;
    }

    public Boolean getShowRestpensjon() {
        return showRestpensjon;
    }

    public void setShowRestpensjon(Boolean showRestpensjon) {
        this.showRestpensjon = showRestpensjon;
    }
}
