package no.nav.pensjon.selvbetjeningopptjening.opptjening;

public class OpptjeningPensjonspoengMerknadDto {
    private String skjermbildetekst;
    private boolean showHelpText;
    private String tooltip;
    private String hjelpetekst;
    private String alternativHjelpeikontekst;
    private String helpPopupUrl;

    public String getSkjermbildetekst() {
        return skjermbildetekst;
    }

    public void setSkjermbildetekst(String skjermbildetekst) {
        this.skjermbildetekst = skjermbildetekst;
    }

    public boolean isShowHelpText() {
        return showHelpText;
    }

    public void setShowHelpText(boolean showHelpText) {
        this.showHelpText = showHelpText;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getHjelpetekst() {
        return hjelpetekst;
    }

    public void setHjelpetekst(String hjelpetekst) {
        this.hjelpetekst = hjelpetekst;
    }

    public String getAlternativHjelpeikontekst() {
        return alternativHjelpeikontekst;
    }

    public void setAlternativHjelpeikontekst(String alternativHjelpeikontekst) {
        this.alternativHjelpeikontekst = alternativHjelpeikontekst;
    }

    public String getHelpPopupUrl() {
        return helpPopupUrl;
    }

    public void setHelpPopupUrl(String helpPopupUrl) {
        this.helpPopupUrl = helpPopupUrl;
    }
}
