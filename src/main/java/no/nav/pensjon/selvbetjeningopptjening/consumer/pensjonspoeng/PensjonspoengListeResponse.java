package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.PensjonspoengDto;

public class PensjonspoengListeResponse {
    private List<PensjonspoengDto> pensjonspoeng = new ArrayList<>();

    public List<PensjonspoengDto> getPensjonspoeng() {
        return pensjonspoeng;
    }

    public void setPensjonspoeng(List<PensjonspoengDto> pensjonspoeng) {
        if (pensjonspoeng != null) {
            this.pensjonspoeng = new ArrayList<>(pensjonspoeng);
        }
    }
}
