package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model;

import java.util.List;

public class PdlMetadata {
    List<PdlMetadataEndring> endringer;

    public List<PdlMetadataEndring> getEndringer() {
        return endringer;
    }

    public void setEndringer(List<PdlMetadataEndring> endringer) {
        this.endringer = endringer;
    }
}
