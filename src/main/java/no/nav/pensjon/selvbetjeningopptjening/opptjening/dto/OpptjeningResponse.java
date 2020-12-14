package no.nav.pensjon.selvbetjeningopptjening.opptjening.dto;

import java.util.Map;

public class OpptjeningResponse {

    private Map<Integer, OpptjeningDto> opptjeningData;
    private Integer numberOfYearsWithPensjonspoeng;
    private Integer fodselsaar;
    private int andelPensjonBasertPaBeholdning;

    public OpptjeningResponse(Integer fodselsaar, int andelPensjonBasertPaBeholdning) {
        this.fodselsaar = fodselsaar;
        this.andelPensjonBasertPaBeholdning = andelPensjonBasertPaBeholdning;
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

    public int getAndelPensjonBasertPaBeholdning() {
        return andelPensjonBasertPaBeholdning;
    }
}
