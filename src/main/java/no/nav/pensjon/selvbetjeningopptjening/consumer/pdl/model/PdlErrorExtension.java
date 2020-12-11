package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

public class PdlErrorExtension {

    private String code;
    private String classification;

    public String getCode() {
        return code;
    }

    public String getClassification() {
        return classification;
    }

    public String toString() {
        return "{"
                + "code: " + this.code + ", "
                + "classification: " + this.classification
                + "}";
    }
}
