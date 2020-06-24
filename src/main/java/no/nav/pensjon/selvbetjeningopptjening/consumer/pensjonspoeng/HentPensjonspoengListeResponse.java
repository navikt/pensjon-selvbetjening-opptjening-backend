package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.consumer.model.Pensjonspoeng;

public class HentPensjonspoengListeResponse {
    private List<Pensjonspoeng> pensjonspoeng = new ArrayList<>();

    public List<Pensjonspoeng> getPensjonspoeng() {
        return pensjonspoeng;
    }

    public void setPensjonspoeng(List<Pensjonspoeng> pensjonspoeng) {
        if (pensjonspoeng != null) {
            this.pensjonspoeng = new ArrayList<>(pensjonspoeng);
        }
    }
}
