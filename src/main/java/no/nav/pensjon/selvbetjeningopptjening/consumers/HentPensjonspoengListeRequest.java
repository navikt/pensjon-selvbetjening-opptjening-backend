package no.nav.pensjon.selvbetjeningopptjening.consumers;


public class HentPensjonspoengListeRequest {

    public HentPensjonspoengListeRequest(String fnr) {
        this.fnr = fnr;
    }

    private String fnr;

    private Integer fomAr;

    private Integer tomAr;

    private String pensjonspoengType;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public Integer getFomAr() {
        return fomAr;
    }

    public void setFomAr(Integer fomAr) {
        this.fomAr = fomAr;
    }

    public Integer getTomAr() {
        return tomAr;
    }

    public void setTomAr(Integer tomAr) {
        this.tomAr = tomAr;
    }

    public String getPensjonspoengType() {
        return pensjonspoengType;
    }

    public void setPensjonspoengType(String pensjonspoengType) {
        this.pensjonspoengType = pensjonspoengType;
    }
}
