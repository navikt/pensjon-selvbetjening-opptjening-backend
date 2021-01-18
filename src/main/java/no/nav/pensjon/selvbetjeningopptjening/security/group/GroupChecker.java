package no.nav.pensjon.selvbetjeningopptjening.security.group;

import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.group.Groups.*;

@Component
public class GroupChecker {

    private static final List<String> AUTHORIZED_GROUPS = List.of(
            BEGRENSET_VEILEDER,
            BRUKERHJELPA,
            KUNDESENTER,
            SAKSBEHANDLER,
            VEILEDER);

    private final GroupApi groupApi;

    public GroupChecker(GroupApi groupApi) {
        this.groupApi = requireNonNull(groupApi);
    }

    public boolean isUserAuthorized(String accessToken) {
        List<Group> memberGroups = groupApi.checkMemberGroups(AUTHORIZED_GROUPS, accessToken);
        return !memberGroups.isEmpty();
    }
}
