package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

public class BeholdningListeRequest {
    private String fnr;

    public BeholdningListeRequest(String fnr) {
        this.fnr = fnr;
    }

    public String getFnr() {
        return fnr;
    }

    public String getBeholdningType() {
        return "PEN_B";
    }

    public String getServiceDirectiveTPOPP006() {
        return "INKL_GRUNNLAG";
    }
}
