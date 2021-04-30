package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

public class Navn {
    private String fornavn;
    private String mellomnavn;
    private String etternavn;

    public Navn(String fornavn, String mellomnavn, String etternavn) {
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }
}
