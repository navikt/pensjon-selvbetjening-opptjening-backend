package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GroupCheckerTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private static final String VEILEDER_GROUP_ID = "959ead5b-99b5-466b-a0ff-5fdbc687517b";
    private static final String UTVIDET_GROUP_ID = "676b5e1f-84e6-46e5-8814-04233699ed4b";
    private GroupChecker checker;

    @Mock
    SkjermingApi skjermingApi;

    @BeforeEach
    void setUp() {
        checker = new GroupChecker(skjermingApi);
    }

    @Test
    void isUserAuthorized_returnsTrue_when_userIsMemberOfAuthorizedGroup() {
        skjermet(false);
        assertTrue(checker.isUserAuthorized(PID, List.of(VEILEDER_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_userIsNotMemberOfAuthorizedGroup() {
        skjermet(false);
        assertFalse(checker.isUserAuthorized(PID, Collections.emptyList()));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_skjermet_and_userIsNotMemberOfUtvidet() {
        skjermet(true);
        assertFalse(checker.isUserAuthorized(PID, List.of(VEILEDER_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_notSkjermet_and_userIsOnlyMemberOfUtvidet() {
        skjermet(false);
        assertFalse(checker.isUserAuthorized(PID, List.of(UTVIDET_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_skjermet_and_userIsOnlyMemberOfUtvidet() {
        skjermet(true);
        assertFalse(checker.isUserAuthorized(PID, List.of(UTVIDET_GROUP_ID)));
    }

    @Test
    void isUserAuthorized_returnsTrue_when_skjermet_and_userIsMemberOfUtvidet() {
        skjermet(true);
        assertTrue(checker.isUserAuthorized(PID, List.of(VEILEDER_GROUP_ID, UTVIDET_GROUP_ID)));
    }

    private void skjermet(boolean value) {
        when(skjermingApi.isSkjermet(PID)).thenReturn(value);
    }
}
