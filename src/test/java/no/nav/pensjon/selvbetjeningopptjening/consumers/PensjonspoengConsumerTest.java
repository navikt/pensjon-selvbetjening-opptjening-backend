package no.nav.pensjon.selvbetjeningopptjening.consumers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PensjonspoengConsumerTest {

    @Test
    void hentPensjonspoengListe() {
        PensjonspoengConsumer pensjonspoengConsumer = new PensjonspoengConsumer();
        HentPensjonspoengListeResponse response = pensjonspoengConsumer.hentPensjonspoengListe(new HentPensjonspoengListeRequest("23115225588"));
        System.out.println(response);
    }
}