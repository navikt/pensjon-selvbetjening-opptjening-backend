package no.nav.pensjon.selvbetjeningopptjening.security.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GroupCheckerTest {

    @Mock
    GroupApi groupApi;

    @Test
    void isUserAuthorized_returnsTrue_when_userIsMember() {
        when(groupApi.checkMemberGroups(any(), eq("token"))).thenReturn(List.of(new Group("id")));
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized("token");
        assertTrue(authorized);
    }

    @Test
    void isUserAuthorized_returnsFalse_when_userIsNotMember() {
        when(groupApi.checkMemberGroups(any(), eq("token"))).thenReturn(Collections.emptyList());
        boolean authorized = new GroupChecker(groupApi).isUserAuthorized("token");
        assertFalse(authorized);
    }
}
