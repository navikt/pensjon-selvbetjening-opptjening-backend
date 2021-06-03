package no.nav.pensjon.selvbetjeningopptjening.security.group;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class AadGroupTest {

    @Test
    void getId_returns_groupId() {
        assertEquals("959ead5b-99b5-466b-a0ff-5fdbc687517b", AadGroup.VEILEDER.getId());
    }

    @Test
    void getName_returns_groupName() {
        assertEquals("0000-GA-Pensjon_UTVIDET", AadGroup.UTVIDET.getName());
    }

    @Test
    void hasAccessToSkjermede_returns_whetherGroupHasAccessToSkjermede() {
        assertFalse(AadGroup.OKONOMI.hasAccessToSkjermede());
    }

    @Test
    void exists_returns_true_when_groupWithGivenIdExists() {
        assertTrue(AadGroup.exists("70ef8e7f-7456-4298-95e0-b13c0ef2422b"));
    }

    @Test
    void exists_returns_false_when_groupWithGivenIdDoesNotExist() {
        assertFalse(AadGroup.exists("no such group"));
    }

    @Test
    void findById_returns_id_when_found() {
        assertEquals(AadGroup.BRUKERHJELPA, AadGroup.findById("7845a796-1516-4d14-b500-fd65c001f35c"));
    }

    @Test
    void findById_throws_NoSuchElementException_when_notFound() {
        var exception = assertThrows(NoSuchElementException.class, () -> AadGroup.findById("undefined"));
        assertEquals("No 'undefined' value present", exception.getMessage());
    }
}
