package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;

public class OpptjeningDto {
    private Integer ar;
    private Integer pensjonsgivendeInntekt;
    private Long pensjonsbeholdning;
    private Double omsorgspoeng;
    private String omsorgspoengType;
    private Double pensjonspoeng;
    private List<MerknadCode> merknad;
    private int maksUforegrad;
    private Double registrertePensjonspoeng;
    private Double restpensjon;
    private List<EndringPensjonsopptjeningDto> endringOpptjening;

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

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

    public int getMaksUforegrad() {
        return maksUforegrad;
    }

    public void setMaksUforegrad(int maksUforegrad) {
        this.maksUforegrad = maksUforegrad;
    }

    public Double getRegistrertePensjonspoeng() {
        return registrertePensjonspoeng;
    }

    public void setRegistrertePensjonspoeng(Double registrertePensjonspoeng) {
        this.registrertePensjonspoeng = registrertePensjonspoeng;
    }

    public Double getRestpensjon() {
        return restpensjon;
    }

    public void setRestpensjon(Double restpensjon) {
        this.restpensjon = restpensjon;
    }

    public List<MerknadCode> getMerknad() {
        return merknad;
    }

    public void setMerknad(List<MerknadCode> merknad) {
        this.merknad = merknad;
    }

    public List<EndringPensjonsopptjeningDto> getEndringOpptjening() {
        return endringOpptjening;
    }

    public void setEndringOpptjening(List<EndringPensjonsopptjeningDto> endringOpptjening) {
        this.endringOpptjening = endringOpptjening;
    }
}
