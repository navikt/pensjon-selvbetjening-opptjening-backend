package no.nav.pensjon.selvbetjeningopptjening.opptjening.model;

import java.util.List;

public class OpptjeningData {
    private Integer ar;

    private Integer pensjonsgivendeInntekt;

    private Long pensjonsbeholdning;

    private Double gjennomsnittligG;

    private Double omsorgspoeng;

    private String omsorgspoengType;

    private Double pensjonspoeng;

    private boolean visMerknad = false;

    private List<OpptjeningPensjonspoengMerknad> merknad;

    private String hjelpMerknad;

    private boolean hideHjelpMerknad;

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

    public boolean isVisMerknad() {
        return visMerknad;
    }

    public void setVisMerknad(boolean visMerknad) {
        this.visMerknad = visMerknad;
    }

    public String getHjelpMerknad() {
        return hjelpMerknad;
    }

    public void setHjelpMerknad(String hjelpMerknad) {
        this.hjelpMerknad = hjelpMerknad;
    }

    public boolean isHideHjelpMerknad() {
        return hideHjelpMerknad;
    }

    public void setHideHjelpMerknad(boolean hideHjelpMerknad) {
        this.hideHjelpMerknad = hideHjelpMerknad;
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

    public List<OpptjeningPensjonspoengMerknad> getMerknad() {
        return merknad;
    }

    public void setMerknad(List<OpptjeningPensjonspoengMerknad> merknad) {
        this.merknad = merknad;
    }
}
