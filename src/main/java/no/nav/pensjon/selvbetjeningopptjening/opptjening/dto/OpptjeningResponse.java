package no.nav.pensjon.selvbetjeningopptjening.opptjening.dto;

import java.util.Map;

public class OpptjeningResponse {

    private Map<Integer, OpptjeningDto> opptjeningData;
    private Integer numberOfYearsWithPensjonspoeng;
    private Integer fodselsaar;

    public OpptjeningResponse(Integer fodselsaar) {
        this.fodselsaar = fodselsaar;
    }

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
