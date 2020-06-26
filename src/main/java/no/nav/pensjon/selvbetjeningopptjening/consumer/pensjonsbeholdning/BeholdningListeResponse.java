package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;

public class BeholdningListeResponse {
    private List<Beholdning> beholdninger = new ArrayList<>();

    public List<Beholdning> getBeholdninger() {
        return beholdninger;
    }

    public void setBeholdninger(List<Beholdning> beholdninger) {
        this.beholdninger = beholdninger;
    }
}
