package no.nav.pensjon.selvbetjeningopptjening.security.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GroupCheckerTest {

    private static final String TOKEN = "token";

    @Mock
    GroupApi groupApi;

    @Test
    void isUserAuthorized_returnsTrue_when_userIsMemberOfAuthorizedGroup() {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(List.of(AadGroup.VEILEDER));
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized(TOKEN, false);
        assertTrue(authorized);
    }

    @Test
    void isUserAuthorized_returnsFalse_when_userIsNotMemberOfAuthorizedGroup() {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(emptyList());
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized(TOKEN, false);
        assertFalse(authorized);
    }

    @Test
    void isUserAuthorized_returnsFalse_when_skjermet_and_userIsNotMemberOfUtvidet() {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(List.of(AadGroup.VEILEDER));
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized(TOKEN, true);
        assertFalse(authorized);
    }

    @Test
    void isUserAuthorized_returnsFalse_when_notSkjermet_and_userIsOnlyMemberOfUtvidet() {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(List.of(AadGroup.UTVIDET));
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized(TOKEN, false);
        assertFalse(authorized);
    }

    @Test
    void isUserAuthorized_returnsFalse_when_skjermet_and_userIsOnlyMemberOfUtvidet() {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(List.of(AadGroup.UTVIDET));
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized(TOKEN, true);
        assertFalse(authorized);
    }

    @Test
    void isUserAuthorized_returnsTrue_when_skjermet_and_userIsMemberOfUtvidet() {
        when(groupApi.checkMemberGroups(any(), eq(TOKEN))).thenReturn(List.of(AadGroup.VEILEDER, AadGroup.UTVIDET));
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized(TOKEN, true);
        assertTrue(authorized);
    }
}
