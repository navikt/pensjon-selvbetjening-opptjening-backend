package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;

public class BeholdningListeResponse {
    private List<BeholdningDto> beholdninger = new ArrayList<>();

    public List<BeholdningDto> getBeholdninger() {
        return beholdninger;
    }

    public void setBeholdninger(List<BeholdningDto> beholdninger) {
        this.beholdninger = beholdninger;
    }
}
