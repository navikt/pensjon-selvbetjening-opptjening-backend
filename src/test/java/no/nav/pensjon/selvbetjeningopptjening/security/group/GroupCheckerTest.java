package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup.UTVIDET;
import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup.VEILEDER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GroupCheckerTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private static final String TOKEN = "token";
    private GroupChecker checker;

    @Mock
    GroupApi groupApi;
    @Mock
    SkjermingApi skjermingApi;

    @BeforeEach
    void setUp() {
        checker = new GroupChecker(groupApi, skjermingApi);
    }

    @Test
    void isUserAuthorized_returnsTrue_when_userIsMemberOfAuthorizedGroup() {
        membership(VEILEDER);
        skjermet(false);
        assertTrue(checker.isUserAuthorized(PID, TOKEN));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_userIsNotMemberOfAuthorizedGroup() {
        membership();
        skjermet(false);
        assertFalse(checker.isUserAuthorized(PID, TOKEN));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_skjermet_and_userIsNotMemberOfUtvidet() {
        membership(VEILEDER);
        skjermet(true);
        assertFalse(checker.isUserAuthorized(PID, TOKEN));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_notSkjermet_and_userIsOnlyMemberOfUtvidet() {
        membership(UTVIDET);
        skjermet(false);
        assertFalse(checker.isUserAuthorized(PID, TOKEN));
    }

    @Test
    void isUserAuthorized_returnsFalse_when_skjermet_and_userIsOnlyMemberOfUtvidet() {
        membership(UTVIDET);
        skjermet(true);
        assertFalse(checker.isUserAuthorized(PID, TOKEN));
    }

    @Test
    void isUserAuthorized_returnsTrue_when_skjermet_and_userIsMemberOfUtvidet() {
        membership(VEILEDER, UTVIDET);
        skjermet(true);
        assertTrue(checker.isUserAuthorized(PID, TOKEN));
    }

    private void membership(AadGroup... groups) {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(asList(groups));
    }

    private void skjermet(boolean value) {
        when(skjermingApi.isSkjermet(PID)).thenReturn(value);
    }
}
