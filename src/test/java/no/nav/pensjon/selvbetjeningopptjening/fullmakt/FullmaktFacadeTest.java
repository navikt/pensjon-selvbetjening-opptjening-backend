package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FullmaktFacadeTest {

    private static final String IRRELEVANT_PID = "01029345678";
    private static final String FULLMAKTSGIVER_PID = "04925398980";
    private static final String FULLMEKTIG_PID = "30915399246";
    private FullmaktFacade facade;

    @Mock
    FullmaktApi service;

    @BeforeEach
    void initialize() {
        facade = new FullmaktFacade(service);
    }

    @Test
    void mayActOnBehalfOf_isTrue_when_fullmektig_has_fullmaktsforhold() {
        when(service.fetchRepresentasjonsgyldighet(FULLMAKTSGIVER_PID)).thenReturn(new RepresentasjonValidity(true, "", "", ""));
        assertTrue(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID).hasValidRepresentasjonsforhold());
    }

    @Test
    void mayActOnBehalfOf_isTrue_when_fullmektig_and_fullmaktsgiver_are_same_person() {
        assertTrue(facade.mayActOnBehalfOf(IRRELEVANT_PID).hasValidRepresentasjonsforhold());
    }

    @Test
    void mayActOnBehalfOf_isFalse_when_fullmektig_has_no_fullmaktsforhold() {
        when(service.fetchRepresentasjonsgyldighet(FULLMAKTSGIVER_PID)).thenReturn(new RepresentasjonValidity(false, "", "", ""));
        assertFalse(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID).hasValidRepresentasjonsforhold());
    }
}
