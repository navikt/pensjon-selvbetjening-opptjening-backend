package no.nav.pensjon.selvbetjeningopptjening.consumers;

public class HentPensjonspoengListeRequest {

    private final String fnr;

    public HentPensjonspoengListeRequest(String fnr) {
        this.fnr = fnr;
    }

    public String getFnr() {
        return fnr;
    }
}
