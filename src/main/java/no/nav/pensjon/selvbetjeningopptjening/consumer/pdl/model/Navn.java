package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

public class Navn {
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private PdlMetadata metadata;
    private PdlFolkeregisterMetadata folkeregistermetadata;

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public void setMellomnavn(String mellomnavn) {
        this.mellomnavn = mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public PdlMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PdlMetadata metadata) {
        this.metadata = metadata;
    }

    public PdlFolkeregisterMetadata getFolkeregistermetadata() {
        return folkeregistermetadata;
    }

    public void setFolkeregistermetadata(PdlFolkeregisterMetadata folkeregistermetadata) {
        this.folkeregistermetadata = folkeregistermetadata;
    }
}
