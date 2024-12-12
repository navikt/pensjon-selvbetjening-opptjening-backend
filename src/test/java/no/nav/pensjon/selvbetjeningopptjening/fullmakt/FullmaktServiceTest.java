package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FullmaktServiceTest {
    @Mock
    private FullmaktClient fullmaktClient;

    private FullmaktService service;

    @BeforeEach
    void initialize() {
        service = new FullmaktService(fullmaktClient);
    }

    @Test
    void should_return_false_when_hasValidRepresentasjonsforhold_false(){
        when(fullmaktClient.hasValidRepresentasjonsforhold(any())).thenReturn(new RepresentasjonValidity(false, null));
        boolean result = service.fetchRepresentasjonsgyldighet("");
        assertFalse(result);
    }

    @Test
    void should_return_true_when_should_return_false_when_hasValidRepresentasjonsforhold_false_true(){
        when(fullmaktClient.hasValidRepresentasjonsforhold(any())).thenReturn(new RepresentasjonValidity(true, null));
        boolean result = service.fetchRepresentasjonsgyldighet("");
        assertTrue(result);
    }
}