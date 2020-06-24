package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.util.List;

public class OpptjeningDto {
    private Integer ar;
    private Integer pensjonsgivendeInntekt;
    private Long pensjonsbeholdning;
    private Double gjennomsnittligG;
    private Double omsorgspoeng;
    private String omsorgspoengType;
    private Double pensjonspoeng;
    private List<OpptjeningPensjonspoengMerknadDto> merknad;
    private String hjelpMerknad;
    private int maksUforegrad;
    private Double registrertePensjonspoeng;
    private Double restpensjon;

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

    public Double getGjennomsnittligG() {
        return gjennomsnittligG;
    }

    public void setGjennomsnittligG(Double gjennomsnittligG) {
        this.gjennomsnittligG = gjennomsnittligG;
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

    public String getHjelpMerknad() {
        return hjelpMerknad;
    }

    public void setHjelpMerknad(String hjelpMerknad) {
        this.hjelpMerknad = hjelpMerknad;
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

    public List<OpptjeningPensjonspoengMerknadDto> getMerknad() {
        return merknad;
    }

    public void setMerknad(List<OpptjeningPensjonspoengMerknadDto> merknad) {
        this.merknad = merknad;
    }
}
