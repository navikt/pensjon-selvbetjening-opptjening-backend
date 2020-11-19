package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForstegangstjenesteOpptjeningBelopDto {

    private Long forstegangstjenesteOpptjeningBelopId;
    private Integer ar;
    private Double belop;
    private ForstegangstjenesteDto forstegangstjeneste;
    private List<ForstegangstjenestePeriodeDto> anvendtForstegangstjenestePeriodeListe;

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

    public ForstegangstjenesteDto getForstegangstjeneste() {
        return forstegangstjeneste;
    }

    public void setForstegangstjeneste(ForstegangstjenesteDto forstegangstjeneste) {
        this.forstegangstjeneste = forstegangstjeneste;
    }

    public List<ForstegangstjenestePeriodeDto> getAnvendtForstegangstjenestePeriodeListe() {
        return anvendtForstegangstjenestePeriodeListe;
    }

    public void setAnvendtForstegangstjenestePeriodeListe(List<ForstegangstjenestePeriodeDto> anvendtForstegangstjenestePeriodeListe) {
        this.anvendtForstegangstjenestePeriodeListe = anvendtForstegangstjenestePeriodeListe;
    }
}
