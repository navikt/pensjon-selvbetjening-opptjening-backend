package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.mock.FullmaktBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.pensjon.selvbetjeningopptjening.mock.FullmaktBuilder.TODAY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FullmaktFacadeTest {
//
//    private static final String IRRELEVANT_PID = "01029345678";
//    private static final String FULLMAKTSGIVER_PID = "04925398980";
//    private static final String FULLMEKTIG_PID = "30915399246";
//    private FixedTodayFullmaktFacade facade;
//
//    @Mock
//    FullmaktApi service;
//
//    @BeforeEach
//    void initialize() {
//        facade = new FixedTodayFullmaktFacade(service);
//    }
//
//    @Test
//    void mayActOnBehalfOf_isFalse_when_noFullmakter() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(emptyList());
//        assertFalse(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID, FULLMEKTIG_PID));
//    }
//
//    @Test
//    void mayActOnBehalfOf_isTrue_when_matchingFullmektig_in_primaryService() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(List.of(validFullmakt()));
//        assertTrue(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID, FULLMEKTIG_PID));
//    }
//
//    @Test
//    void mayActOnBehalfOf_isTrue_when_fullmektig_and_fullmaktsgiver_are_same_person() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(emptyList());
//        assertTrue(facade.mayActOnBehalfOf(IRRELEVANT_PID, IRRELEVANT_PID));
//    }
//
//    @Test
//    void mayActOnBehalfOf_isFalse_when_mismatchingFullmaktsgiver() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(List.of(validFullmakt()));
//        assertFalse(facade.mayActOnBehalfOf(IRRELEVANT_PID, FULLMEKTIG_PID));
//    }
//
//    @Test
//    void mayActOnBehalfOf_isFalse_when_mismatchingFullmektig() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(List.of(validFullmakt()));
//        assertFalse(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID, IRRELEVANT_PID));
//    }
//
//    @Test
//    void mayActOnBehalfOf_isFalse_when_invalidFullmakt() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(List.of(fullmakt().withStatusNotGyldig().build()));
//        assertFalse(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID, FULLMEKTIG_PID));
//    }
//
//    @Test
//    void mayActOnBehalfOf_isTrue_when_oneOfMany_fullmakter_is_valid() {
//        when(service.getFullmakter(FULLMAKTSGIVER_PID)).thenReturn(List.of(
//                fullmakt().withEndInPast().build(),
//                fullmakt().withStartInFuture().build(),
//                fullmakt().withStatusNotGyldig().build(),
//                validFullmakt(),
//                fullmakt().withNonPersonFullmaktsgiver().build(),
//                fullmakt().withNonPersonFullmektig().build(),
//                fullmakt().withNoFagomraade().build()));
//
//        assertTrue(facade.mayActOnBehalfOf(FULLMAKTSGIVER_PID, FULLMEKTIG_PID));
//    }
//
//    private static Fullmakt validFullmakt() {
//        return fullmakt().build();
//    }
//
//    private static FullmaktBuilder fullmakt() {
//        return new FullmaktBuilder();
//    }
//
//    /**
//     * FullmaktFacade with overridden constant today() value for safer testability.
//     */
//    private static class FixedTodayFullmaktFacade extends FullmaktFacade {
//
//        public FixedTodayFullmaktFacade(FullmaktApi supplementaryService) {
//            super(supplementaryService);
//        }
//
//        @Override
//        protected LocalDate today() {
//            return TODAY;
//        }
//    }
}
