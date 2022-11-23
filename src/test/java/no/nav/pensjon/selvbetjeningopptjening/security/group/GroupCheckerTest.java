package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GroupCheckerTest {

    private static final Pid PID = new Pid("04925398980");
    private static final String BRUKERHJELP_GROUP_ID = "brukerhjelp";
    private static final String OEKONOMI_GROUP_ID = "økonomi";
    private static final String SAKSBEHANDLER_GROUP_ID = "saksbehandler";
    private static final String VEILEDER_GROUP_ID = "veileder";
    private static final String EGNE_ANSATTE_TILGANG_GROUP_ID = "egne ansatte";
    private static final String UTVIDET_GROUP_ID = "utvidet tilgang";
    private GroupChecker checker;

    @Mock
    private SkjermingApi skjermingApi;

    @BeforeEach
    void setUp() {
        checker = new GroupChecker(
                skjermingApi,
                BRUKERHJELP_GROUP_ID,
                OEKONOMI_GROUP_ID,
                SAKSBEHANDLER_GROUP_ID,
                VEILEDER_GROUP_ID,
                EGNE_ANSATTE_TILGANG_GROUP_ID,
                UTVIDET_GROUP_ID);
    }

    @Test
    void isUserAuthorized_returns_true_when_user_is_member_of_authorized_group() {
        arrangeSkjermet(false);
        assertTrue(checker.isUserAuthorized(PID, List.of(BRUKERHJELP_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returns_false_when_user_is_not_member_of_authorized_group() {
        arrangeSkjermet(false);
        assertFalse(checker.isUserAuthorized(PID, emptyList()));
    }

    @Test
    void isUserAuthorized_returns_false_when_user_is_member_of_utvidet_group_and_nonAuthorized_group() {
        arrangeSkjermet(false);
        assertFalse(checker.isUserAuthorized(PID, List.of(UTVIDET_GROUP_ID, "not authorized")));
    }

    @Test
    void isUserAuthorized_returns_false_when_skjermet_and_user_is_not_member_of_utvidet() {
        arrangeSkjermet(true);
        assertFalse(checker.isUserAuthorized(PID, List.of(SAKSBEHANDLER_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returns_false_when_notSkjermet_and_user_is_only_member_of_utvidet() {
        arrangeSkjermet(false);
        assertFalse(checker.isUserAuthorized(PID, List.of(UTVIDET_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returns_false_when_skjermet_and_user_is_only_member_of_utvidet() {
        arrangeSkjermet(true);
        assertFalse(checker.isUserAuthorized(PID, List.of(UTVIDET_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returns_true_when_skjermet_and_user_is_member_of_utvidet() {
        arrangeSkjermet(true);
        assertTrue(checker.isUserAuthorized(PID, List.of(VEILEDER_GROUP_ID, UTVIDET_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returns_true_when_skjermet_and_user_is_member_of_egneAnsatte() {
        arrangeSkjermet(true);
        assertTrue(checker.isUserAuthorized(PID, List.of(OEKONOMI_GROUP_ID, EGNE_ANSATTE_TILGANG_GROUP_ID)));
    }

    private void arrangeSkjermet(boolean value) {
        when(skjermingApi.isSkjermet(PID)).thenReturn(value);
    }
}
