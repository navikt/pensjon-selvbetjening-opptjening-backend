package no.nav.pensjon.selvbetjeningopptjening.opptjening.dto;

import no.nav.pensjon.selvbetjeningopptjening.person.Person;

import java.util.Map;

public class OpptjeningResponse {

    private Map<Integer, OpptjeningDto> opptjeningData;
    private Integer numberOfYearsWithPensjonspoeng;
    private final Integer fodselsaar;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final int andelPensjonBasertPaBeholdning;
    private final String pid;
    private final String fullmektigPid;

    public OpptjeningResponse(Person person, int andelPensjonBasertPaBeholdning, String pid, String fullmektigPid) {
        fodselsaar = person.getFodselsdato().getYear();
        fornavn = person.getFornavn();
        mellomnavn = person.getMellomnavn();
        etternavn = person.getEtternavn();
        this.andelPensjonBasertPaBeholdning = andelPensjonBasertPaBeholdning;
        this.pid = pid;
        this.fullmektigPid = fullmektigPid;
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

    public String getPid() {
        return pid;
    }

    public String getFullmektigPid() {
        return fullmektigPid;
    }
}
