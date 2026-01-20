package no.nav.pensjon.selvbetjeningopptjening.logging;

public record LogMessage(String type, Object jsonContent) {

    public Object getJsonContent() {
        return jsonContent;
    }

    public String getType() {
        return type;
    }
}