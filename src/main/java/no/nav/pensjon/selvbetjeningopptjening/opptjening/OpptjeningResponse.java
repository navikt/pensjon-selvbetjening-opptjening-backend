package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.Map;

public class OpptjeningResponse {

    public OpptjeningResponse(Integer fodselsaar) {
        this.fodselsaar = fodselsaar;
    }

    private Map<Integer, OpptjeningDto> opptjeningData;

    private Integer numberOfYearsWithPensjonspoeng;

    private Integer fodselsaar;

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

    public Integer getFodselsaar() {
        return fodselsaar;
    }
}
