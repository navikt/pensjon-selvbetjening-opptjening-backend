package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class PdlError {
    private String message;
    private List<PdlErrorLocation> locations;
    private List<String> path;
    private PdlErrorExtension extensions;

    public String getMessage() {
        return message;
    }

    public List<PdlErrorLocation> getLocations() {
        return locations;
    }

    public List<String> getPath() {
        return path;
    }

    public PdlErrorExtension getExtensions() {
        return extensions;
    }

    public void setExtensions(PdlErrorExtension extensions) {
        this.extensions = extensions;
    }

    public String toString() {
        return "{"
                + "message: " + this.message + ", "
                + "locations: " + this.locations.toString() + ", "
                + "extensions: " + this.extensions.toString() + ", "
                + "path: " + this.path.toString()
                + "}";
    }

    static class PdlErrorLocation {
        private Integer line;
        private Integer column;

        public Integer getLine() {
            return line;
        }

        public Integer getColumn() {
            return column;
        }

        public String toString() {
            return "{"
                    + "line: " + this.line + ", "
                    + "column: " + this.column
                    + "}";
        }
    }

    static class PdlErrorExtension {
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
}
