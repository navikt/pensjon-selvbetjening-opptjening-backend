package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.mock.FullmaktBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FullmaktTest {

    private static final String FULLMAKTSGIVER_PID = "01865499538";
    private static final String FULLMEKTIG_PID = "05845997316";
    private static final String IRRELEVANT_PID = "30915399246";

    @Test
    void test_lastsForever() {
        assertFalse(new FullmaktBuilder().withEndInFuture().build().lastsForever());
        assertTrue(new FullmaktBuilder().withNoEnd().build().lastsForever());
    }

    @Test
    void test_isGyldig() {
        assertTrue(new FullmaktBuilder().withStatusGyldig().build().isGyldig());
        assertFalse(new FullmaktBuilder().withStatusNotGyldig().build().isGyldig());
    }

    @Test
    void test_getters() {
        Fullmakt fullmakt = new FullmaktBuilder()
                .withFagomraade(Fagomraade.ALL)
                .withNivaa(Fullmaktnivaa.SAMORDPLIK)
                .withVersjon(123)
                .build();

        assertEquals(Fagomraade.ALL, fullmakt.getFagomrade());
        assertEquals(Fullmaktnivaa.SAMORDPLIK, fullmakt.getNivaa());
        assertEquals(123, fullmakt.getVersjon());
    }

    @Test
    void isValidFor_is_false_when_wrong_fullmaktsgiver() {
        Fullmakt fullmakt = new FullmaktBuilder()
                .withFullmaktsgiverPid(IRRELEVANT_PID)
                .withFullmektigPid(FULLMEKTIG_PID)
                .withStartInPast()
                .withEndInFuture()
                .build();

        assertFalse(fullmakt.isValidFor(FULLMAKTSGIVER_PID, FULLMEKTIG_PID, FullmaktBuilder.TODAY));
    }

    @Test
    void isValidFor_is_false_when_wrong_fullmektig() {
        Fullmakt fullmakt = new FullmaktBuilder()
                .withFullmaktsgiverPid(FULLMAKTSGIVER_PID)
                .withFullmektigPid(IRRELEVANT_PID)
                .withStartInPast()
                .withEndInFuture()
                .build();

        assertFalse(fullmakt.isValidFor(FULLMAKTSGIVER_PID, FULLMEKTIG_PID, FullmaktBuilder.TODAY));
    }

    @Test
    void isValidFor_is_false_when_fullmakt_not_yet_valid() {
        Fullmakt fullmakt = new FullmaktBuilder()
                .withFullmaktsgiverPid(FULLMAKTSGIVER_PID)
                .withFullmektigPid(FULLMEKTIG_PID)
                .withStartInFuture() // <---- i.e. not yet valid
                .withEndInFuture()
                .build();

        assertFalse(fullmakt.isValidFor(FULLMAKTSGIVER_PID, FULLMEKTIG_PID, FullmaktBuilder.TODAY));
    }

    @Test
    void isValidFor_is_true_when_fullmakt_expired() {
        Fullmakt fullmakt = new FullmaktBuilder()
                .withFullmaktsgiverPid(FULLMAKTSGIVER_PID)
                .withFullmektigPid(FULLMEKTIG_PID)
                .withStartInPast()
                .withEndInPast() // <---- i.e. expired
                .build();

        assertFalse(fullmakt.isValidFor(FULLMAKTSGIVER_PID, FULLMEKTIG_PID, FullmaktBuilder.TODAY));
    }

    @Test
    void isValidFor_is_true_when_all_criteria_met() {
        Fullmakt fullmakt = new FullmaktBuilder()
                .withFullmaktsgiverPid(FULLMAKTSGIVER_PID)
                .withFullmektigPid(FULLMEKTIG_PID)
                .withStartInPast()
                .withEndInFuture()
                .build();

        assertTrue(fullmakt.isValidFor(FULLMAKTSGIVER_PID, FULLMEKTIG_PID, FullmaktBuilder.TODAY));
    }
}
