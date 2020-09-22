package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlError;

public class PdlResponse {

    private PdlData data;
    private List<PdlError> errors;

    public PdlData getData() {
        return data;
    }

    public void setData(PdlData data) {
        this.data = data;
    }

    public List<PdlError> getErrors() {
        return errors;
    }

    public void setErrors(List<PdlError> errors) {
        this.errors = errors;
    }
}
