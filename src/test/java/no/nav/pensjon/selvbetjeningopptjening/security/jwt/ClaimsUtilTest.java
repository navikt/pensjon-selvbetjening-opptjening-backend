package no.nav.pensjon.selvbetjeningopptjening.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ClaimsUtilTest {

    @Mock
    private Claims claims;

    @Test
    void getGroups_extracts_groups_from_claims() {
        when(claims.get("groups")).thenReturn(List.of("group1", "group2"));

        List<String> groups = ClaimsUtil.getGroups(claims);

        assertEquals(2, groups.size());
        assertTrue(groups.contains("group1"));
        assertTrue(groups.contains("group2"));
    }

    @Test
    void getGroups_returns_emptyList_when_no_groups_in_claims() {
        when(claims.get("groups")).thenReturn(null);
        List<String> groups = ClaimsUtil.getGroups(claims);
        assertTrue(groups.isEmpty());
    }

    @Test
    void getInternalUserId_extracts_internalUserId_from_claims() {
        when(claims.get("NAVident")).thenReturn("user1");
        String userId = ClaimsUtil.getInternalUserId(claims);
        assertEquals("user1", userId);
    }

    @Test
    void getInternalUserId_returns_emptyString_when_no_internalUserId_in_claims() {
        when(claims.get("NAVident")).thenReturn(null);
        String userId = ClaimsUtil.getInternalUserId(claims);
        assertEquals("", userId);
    }
}
