package no.nav.pensjon.selvbetjeningopptjening.opptjening.dto;

import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;

public class OpptjeningDto {

    private Integer pensjonsgivendeInntekt;
    private Long pensjonsbeholdning;
    private Double omsorgspoeng;
    private String omsorgspoengType;
    private Double pensjonspoeng;
    private List<MerknadCode> merknader = new ArrayList<>();
    private Double restpensjon;
    private int maksUforegrad;
    private List<EndringPensjonsopptjeningDto> endringOpptjening;

    public Integer getPensjonsgivendeInntekt() {
        return pensjonsgivendeInntekt;
    }

    public void setPensjonsgivendeInntekt(Integer pensjonsgivendeInntekt) {
        this.pensjonsgivendeInntekt = pensjonsgivendeInntekt;
    }

    public Long getPensjonsbeholdning() {
        return pensjonsbeholdning;
    }

    public void setPensjonsbeholdning(Long pensjonsbeholdning) {
        this.pensjonsbeholdning = pensjonsbeholdning;
    }

    public Double getOmsorgspoeng() {
        return omsorgspoeng;
    }

    public void setOmsorgspoeng(Double omsorgspoeng) {
        this.omsorgspoeng = omsorgspoeng;
    }

    public String getOmsorgspoengType() {
        return omsorgspoengType;
    }

    public void setOmsorgspoengType(String omsorgspoengType) {
        this.omsorgspoengType = omsorgspoengType;
    }

    public Double getPensjonspoeng() {
        return pensjonspoeng;
    }

    public void setPensjonspoeng(Double pensjonspoeng) {
        this.pensjonspoeng = pensjonspoeng;
    }

    public Double getRestpensjon() {
        return restpensjon;
    }

    public void setRestpensjon(Double restpensjon) {
        this.restpensjon = restpensjon;
    }

    public List<MerknadCode> getMerknader() {
        return merknader;
    }

    public void setMerknader(List<MerknadCode> merknader) {
        this.merknader = merknader;
    }

    public int getMaksUforegrad() {
        return maksUforegrad;
    }

    public void setMaksUforegrad(int maksUforegrad) {
        this.maksUforegrad = maksUforegrad;
    }

    public List<EndringPensjonsopptjeningDto> getEndringOpptjening() {
        return endringOpptjening;
    }

    public void setEndringOpptjening(List<EndringPensjonsopptjeningDto> endringOpptjening) {
        this.endringOpptjening = endringOpptjening;
    }

    public void addMerknader(List<MerknadCode> merknader) {
        this.merknader.addAll(merknader);
    }
}
