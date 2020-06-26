package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForstegangstjenesteOpptjeningBelop  {

    private Long forstegangstjenesteOpptjeningBelopId;

    private Integer ar;

    private Double belop;

    private Forstegangstjeneste forstegangstjeneste;

    private List<ForstegangstjenestePeriode> anvendtForstegangstjenestePeriodeListe;

    public Long getForstegangstjenesteOpptjeningBelopId() {
        return forstegangstjenesteOpptjeningBelopId;
    }

    public void setForstegangstjenesteOpptjeningBelopId(Long forstegangstjenesteOpptjeningBelopId) {
        this.forstegangstjenesteOpptjeningBelopId = forstegangstjenesteOpptjeningBelopId;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Double getBelop() {
        return belop;
    }

    public void setBelop(Double belop) {
        this.belop = belop;
    }

    public Forstegangstjeneste getForstegangstjeneste() {
        return forstegangstjeneste;
    }

    public void setForstegangstjeneste(Forstegangstjeneste forstegangstjeneste) {
        this.forstegangstjeneste = forstegangstjeneste;
    }

    public List<ForstegangstjenestePeriode> getAnvendtForstegangstjenestePeriodeListe() {
        return anvendtForstegangstjenestePeriodeListe;
    }

    public void setAnvendtForstegangstjenestePeriodeListe(List<ForstegangstjenestePeriode> anvendtForstegangstjenestePeriodeListe) {
        this.anvendtForstegangstjenestePeriodeListe = anvendtForstegangstjenestePeriodeListe;
    }
}
