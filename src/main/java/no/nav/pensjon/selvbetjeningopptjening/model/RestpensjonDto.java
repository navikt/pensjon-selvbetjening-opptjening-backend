package no.nav.pensjon.selvbetjeningopptjening.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestpensjonDto {

    private String fnr;
    private Double restGrunnpensjon;
    private Double restTilleggspensjon;
    private Double restPensjonstillegg;
    private Long vedtakId;
    private LocalDate fomDato;
    private LocalDate tomDato;
    private List<PensjonspoengDto> pensjonspoengListe;
    private String oppdateringArsak;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public Double getRestGrunnpensjon() {
        return restGrunnpensjon;
    }

    public void setRestGrunnpensjon(Double restGrunnpensjon) {
        this.restGrunnpensjon = restGrunnpensjon;
    }

    public Double getRestTilleggspensjon() {
        return restTilleggspensjon;
    }

    public void setRestTilleggspensjon(Double restTilleggspensjon) {
        this.restTilleggspensjon = restTilleggspensjon;
    }

    public Double getRestPensjonstillegg() {
        return restPensjonstillegg;
    }

    public void setRestPensjonstillegg(Double restPensjonstillegg) {
        this.restPensjonstillegg = restPensjonstillegg;
    }

    public Long getVedtakId() {
        return vedtakId;
    }

    public void setVedtakId(Long vedtakId) {
        this.vedtakId = vedtakId;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public void setFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public void setTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
    }

    public List<PensjonspoengDto> getPensjonspoengListe() {
        return pensjonspoengListe;
    }

    public void setPensjonspoengListe(List<PensjonspoengDto> pensjonspoengListe) {
        this.pensjonspoengListe = pensjonspoengListe;
    }

    public String getOppdateringArsak() {
        return oppdateringArsak;
    }

    public void setOppdateringArsak(String oppdateringArsak) {
        this.oppdateringArsak = oppdateringArsak;
    }
}
