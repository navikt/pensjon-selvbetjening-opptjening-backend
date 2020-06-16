package no.nav.pensjon.selvbetjeningopptjening.consumers;

import java.util.ArrayList;
import java.util.List;

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
