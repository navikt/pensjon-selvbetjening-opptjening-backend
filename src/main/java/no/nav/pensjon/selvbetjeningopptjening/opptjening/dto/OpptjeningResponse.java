package no.nav.pensjon.selvbetjeningopptjening.opptjening.dto;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;

import java.util.Map;

public class OpptjeningResponse {

    private Map<Integer, OpptjeningDto> opptjeningData;
    private Integer numberOfYearsWithPensjonspoeng;
    private Integer fodselsaar;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private int andelPensjonBasertPaBeholdning;

    public OpptjeningResponse(Person person, int andelPensjonBasertPaBeholdning) {
        fodselsaar = person.getFodselsdato().getYear();
        fornavn = person.getFornavn();
        mellomnavn = person.getMellomnavn();
        etternavn = person.getEtternavn();
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

    public String getFornavn() {
        return fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public int getAndelPensjonBasertPaBeholdning() {
        return andelPensjonBasertPaBeholdning;
    }
}
