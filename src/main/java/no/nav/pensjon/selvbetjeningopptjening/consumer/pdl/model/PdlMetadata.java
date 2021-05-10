package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class PdlMetadata {
    private String master;
    private List<PdlMetadataEndring> endringer;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public List<PdlMetadataEndring> getEndringer() {
        return endringer;
    }

    public void setEndringer(List<PdlMetadataEndring> endringer) {
        this.endringer = endringer;
    }
}
